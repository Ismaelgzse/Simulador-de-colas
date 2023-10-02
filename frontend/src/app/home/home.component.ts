import {Component, OnInit} from "@angular/core";
import {HomeService} from "./home.service";


import {ModalDismissReasons, NgbDatepickerModule, NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {Folder} from "./folder.model";
import {Simulation} from "./simulation.model";
import {Router} from "@angular/router";
import {FormControl, FormGroup, ReactiveFormsModule, ValidatorFn, Validators} from "@angular/forms";


@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['../../assets/css/home.css', '../../assets/vendor/fontawesome-free-6.4.0-web/css/all.css'],
  providers: [HomeService]
})

export class HomeComponent implements OnInit {
  image?: File;
  folderInfo: Folder;
  simulationInfo: Simulation;
  listFolders: Folder[];
  numFoldersEmpty: boolean;
  newFolderTitleBinding: boolean;
  newSimulationTitleBinding: boolean;
  loading: boolean;

  newFolderForm = new FormGroup({
    folderName: new FormControl('', Validators.compose([Validators.required, Validators.minLength(1)]))
  })

  newSimulationForm = new FormGroup({
    title: new FormControl('', Validators.compose([Validators.required, Validators.minLength(4), Validators.maxLength(20)])),
    body: new FormControl('', Validators.compose([Validators.required, Validators.maxLength(100)])),
    folder: new FormControl(),
  })


  constructor(private modalService: NgbModal, private homeService: HomeService, private router: Router) {
  }

  ngOnInit(): void {
    this.loading = true;
    this.image = undefined;
    this.simulationInfo = {
      title: '',
      body: '',
      imageFile: '',
      statusSimulation:'0'
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
    this.homeService.getFolders().subscribe({
      next: (folders) => {
        this.listFolders = folders;
        if (this.listFolders.length > 0) {
          this.numFoldersEmpty = false;
          for (let i = 0; i < this.listFolders.length; i++) {
            this.listFolders[i].page = 0;
          }
        } else {
          this.numFoldersEmpty = true;
        }
        this.loading = false;
      },
      error: (err) => {
        this.router.navigate(['error403']);
      }
    })
  }

  openModalFolder(content: any, element: number
  ) {
    if (element != -1) {
      this.folderInfo.nameFolder = this.listFolders[element].nameFolder;
      this.folderInfo.idFolder = this.listFolders[element].idFolder;
      this.folderInfo.simulations = this.listFolders[element].simulations;
      this.newFolderForm.patchValue({
        folderName: this.folderInfo.nameFolder
      })
    } else {
      this.folderInfo.nameFolder = '';
      this.folderInfo.simulations = []
      this.newFolderForm.patchValue({
        folderName: this.folderInfo.nameFolder
      })
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
      this.simulationInfo.imageFile = this.listFolders[folder].simulations[simulation].imageFile;
      this.newSimulationForm.patchValue({
        title: this.simulationInfo.title,
        body: this.simulationInfo.body,
        folder: this.simulationInfo.folderId
      })
    } else {
      this.simulationInfo.body = '';
      this.simulationInfo.title = '';
      this.simulationInfo.imageFile = '';
      this.newSimulationForm.patchValue({
        title: '',
        body: '',
        folder: '',
      })
    }
    this.modalService.open(content, {ariaLabelledBy: 'modal-basic-title'})
  }

  saveFolder() {
    this.loading = true;
    if (typeof this.newFolderForm.value.folderName === "string") {
      this.folderInfo.nameFolder = this.newFolderForm.value.folderName;
    }
    if (this.folderInfo.idFolder) {
      this.homeService.updateFolder(this.folderInfo).subscribe({
        next: (folder) => {
          this.loading = false;
          this.ngOnInit();
        },
        error: (err) => {
          this.router.navigate(['error500']);
        }
      })
    } else {
      this.homeService.saveFolder(this.folderInfo).subscribe({
        next: (folder) => {
          this.loading = false;
          this.ngOnInit();
        },
        error: (err) => {
          this.router.navigate(['error500']);
        }
      })
    }
  }

  saveSimulation() {
    this.loading = true;
    this.simulationInfo.title = <string>this.newSimulationForm.value.title;
    this.simulationInfo.body = <string>this.newSimulationForm.value.body;
    this.simulationInfo.folderId = this.newSimulationForm.value.folder;
    if (this.simulationInfo.idSimulation) {
      this.homeService.updateSimulation(this.simulationInfo).subscribe({
        next: (simulation) => {
          if (this.image != null && simulation.idSimulation) {
            const form = new FormData();
            form.append('file', this.image, this.image.name);
            this.homeService.updateImage(simulation.idSimulation, form).subscribe({
              next: (response) => {
                this.ngOnInit();
              },
              error: (err) => {
                this.router.navigate(['error500']);
              }
            })
          }
          this.loading = false;
          this.ngOnInit();
        },
        error: (err) => {
          this.router.navigate(['error500']);
        }
      })
    } else {
      this.simulationInfo.statusSimulation='0';
      this.homeService.saveSimulation(this.simulationInfo).subscribe({
        next: (simulation) => {
          if (this.image != null && simulation.idSimulation) {
            const form = new FormData();
            form.append('file', this.image, this.image.name);
            this.homeService.updateImage(simulation.idSimulation, form).subscribe({
              next: (response) => {
                this.ngOnInit();
              },
              error: (err) => {
                this.router.navigate(['error500']);
              }
            })
          }
          this.loading = false;
          this.ngOnInit();
        },
        error: (err) => {
          this.router.navigate(['error500']);
        }
      })
    }
  }

  deleteFolderFunction() {
    this.loading = true;
    this.modalService.dismissAll();
    if (this.folderInfo.idFolder != undefined) {
      this.homeService.deleteFolder(this.folderInfo.idFolder).subscribe({
        next: (folder) => {
          this.loading = false;
          this.ngOnInit();
        },
        error: (err) => {
          this.router.navigate(['error500']);
        }
      })
    }
  }

  deleteSimulationFunction() {
    this.loading = true;
    this.modalService.dismissAll();
    if (this.simulationInfo.folderId != undefined && this.simulationInfo.idSimulation != undefined) {
      this.homeService.deleteSimulation(this.simulationInfo.folderId, this.simulationInfo.idSimulation).subscribe({
        next: (simulation) => {
          this.loading = false;
          this.ngOnInit();
        },
        error: (err) => {
          this.router.navigate(['error500']);
        }
      })
    }
  }

  loadNewPageSimulations(folder: number) {
    this.loading = true;
    let idFolder = this.listFolders[folder].idFolder;
    let page = this.listFolders[folder].page
    if (idFolder && page != undefined) {
      this.homeService.getPageSimulation(idFolder, page + 1).subscribe({
        next: (simulations) => {
          // @ts-ignore
          this.listFolders[folder].page = page + 1;
          this.listFolders[folder].simulations = this.listFolders[folder].simulations.concat(simulations.content);
          this.listFolders[folder].isLastPage = simulations.last;
          this.loading = false;
        },
        error: (err) => {
          this.router.navigate(['error500']);
        }
      })
    }

  }

  selectImage(event: Event): void {
    // @ts-ignore
    this.image = event.target.files[0];
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

  get folderName() {
    return this.newFolderForm.get('folderName')
  }

  get title() {
    return this.newSimulationForm.get('title')
  }

  get body() {
    return this.newSimulationForm.get('body')
  }

  get folderSimulation() {
    return this.newSimulationForm.get('folder')
  }

}
