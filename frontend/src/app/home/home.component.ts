import {Component, OnInit} from "@angular/core";
import {HomeService} from "./home.service";


import {ModalDismissReasons, NgbDatepickerModule, NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {Folder} from "./folder.model";
import {Simulation} from "./simulation.model";
import {Router} from "@angular/router";


@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['../../assets/css/home.css', '../../assets/vendor/fontawesome-free-6.4.0-web/css/all.css'],
  providers: [HomeService]
})

export class HomeComponent implements OnInit {
  image: File;
  folder: Folder;
  folderInfo: Folder;
  simulationInfo: Simulation;
  listFolders: Folder[];
  numFoldersEmpty: boolean;
  i: number;
  newFolderTitleBinding: boolean;
  newSimulationTitleBinding: boolean;


  constructor(private modalService: NgbModal, private homeService: HomeService, private router:Router) {
  }

  ngOnInit(): void {
    this.simulationInfo = {
      title: '',
      body: '',
      imageFile:''
    }
    this.folderInfo = {
      nameFolder: '',
      simulations: [],
      isLastPage: false
    }
    this.newFolderTitleBinding = false;
    this.newSimulationTitleBinding = false
    this.numFoldersEmpty = false;
    this.listFolders = [];
    this.homeService.getFolders().subscribe(
      (folders => {
        this.listFolders = folders;
        if (this.listFolders.length > 0) {
          this.numFoldersEmpty = false;
          for (this.i = 0; this.i < this.listFolders.length; this.i++) {
            this.listFolders[this.i].page = 0;
          }
        } else {
          this.numFoldersEmpty = true;
        }

      }),
      (error => {
        this.router.navigate(['error403'])
      })
    )
  }

  openModalFolder(content: any, element: number
  ) {
    if (element != -1) {
      this.folderInfo.nameFolder = this.listFolders[element].nameFolder;
      this.folderInfo.idFolder = this.listFolders[element].idFolder;
      this.folderInfo.simulations = this.listFolders[element].simulations
    } else {
      this.folderInfo.nameFolder = '';
      this.folderInfo.simulations = []
    }
    this.modalService.open(content, {ariaLabelledBy: 'modal-basic-title'})
  }

  openModalSimulation(content: any, folder: number, simulation: number
  ) {
    if (folder != -1 && simulation != -1) {
      this.simulationInfo.idSimulation = this.listFolders[folder].simulations[simulation].idSimulation;
      this.simulationInfo.title = this.listFolders[folder].simulations[simulation].title;
      this.simulationInfo.body = this.listFolders[folder].simulations[simulation].body;
      this.simulationInfo.folderId = this.listFolders[folder].idFolder;
      this.simulationInfo.imageFile=this.listFolders[folder].simulations[simulation].imageFile;
    } else {
      this.simulationInfo.body = '';
      this.simulationInfo.title = '';
      this.simulationInfo.imageFile='';
    }
    this.modalService.open(content, {ariaLabelledBy: 'modal-basic-title'})
  }

  saveFolder(mode: number
  ) {
    if (mode == 0) {
      this.homeService.saveFolder(this.folderInfo).subscribe(
        (folder => this.ngOnInit())
      )
    } else {
      this.homeService.updateFolder(this.folderInfo).subscribe(
        (folder => this.ngOnInit())
      )
    }
  }

  saveSimulation(mode: number) {
    if (mode == 0) {
      this.homeService.saveSimulation(this.simulationInfo).subscribe(
        (simulation => {
          if (this.image !== null && simulation.idSimulation) {
            const form = new FormData();
            form.append('file', this.image, this.image.name);
            this.homeService.updateImage(simulation.idSimulation, form).subscribe(
              (response => {
                this.ngOnInit();
              })
            )
          }
          this.ngOnInit();
        })
      )
    } else {
      this.homeService.updateSimulation(this.simulationInfo).subscribe(
        (simulation => {
          if (this.image !== null && simulation.idSimulation) {
            const form = new FormData();
            form.append('file', this.image, this.image.name);
            this.homeService.updateImage(simulation.idSimulation, form).subscribe(
              (response => {
                this.ngOnInit();
              })
            )
          }
          this.ngOnInit();
        })
      )
    }
  }

  deleteFolderFunction() {
    this.modalService.dismissAll();
    if (this.folderInfo.idFolder != undefined) {
      this.homeService.deleteFolder(this.folderInfo.idFolder).subscribe(
        (folder => this.ngOnInit())
      )
    }
  }

  deleteSimulationFunction() {
    this.modalService.dismissAll();
    if (this.simulationInfo.folderId != undefined && this.simulationInfo.idSimulation != undefined) {
      this.homeService.deleteSimulation(this.simulationInfo.folderId, this.simulationInfo.idSimulation).subscribe(
        (simulation => this.ngOnInit())
      )
    }
  }

  loadNewPageSimulations(folder: number) {
    let idFolder = this.listFolders[folder].idFolder;
    let page = this.listFolders[folder].page
    if (idFolder && page != undefined) {
      this.homeService.getPageSimulation(idFolder, page + 1).subscribe(
        (simulations => {
          // @ts-ignore
          this.listFolders[folder].page = page + 1;
          this.listFolders[folder].simulations = this.listFolders[folder].simulations.concat(simulations.content);
          this.listFolders[folder].isLastPage = simulations.last;
        })
      )
    }

  }

  selectImage(event: Event): void {
    // @ts-ignore
    this.image = event.target.files[0];
  }

  open(content: any
  ) {
    this.modalService.open(content, {ariaLabelledBy: 'modal-basic-title'})
  }

  newFolderTitle() {
    this.newFolderTitleBinding = true;
  }

  newSimulationTitle() {
    this.newSimulationTitleBinding = true;
  }

  modifyFolderTitle() {
    this.newFolderTitleBinding = false;
  }

  modifySimulationTilte() {
    this.newSimulationTitleBinding = false;
  }

  save(): void {
    this.modalService.dismissAll();
  }

}
