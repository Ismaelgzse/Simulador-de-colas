contador = 0

let imagenes= document.querySelectorAll(".image");
//console.log(imagenes)
for (var i=0;i<imagenes.length;i++){
    imagenes[i].addEventListener("dragstart", drag);
    imagenes[i].addEventListener("dragend", moveElement);
}
let canvas= document.getElementById("canvas")

function deleteElement(event) {
    var data = event.dataTransfer.getData("text");
    var parentClass = document.getElementById(data).parentNode.className;
    var element = document.getElementById(data)
    if (parentClass !== "dragItemContainer") {
        element.parentNode.removeChild(element);
    }
}

function deleteElement2(id) {
    var element = document.getElementById(id);
    element.parentNode.removeChild(element);
}

function dragOver(event) {
    event.preventDefault()
}

function drag(event) {
    event.dataTransfer.setData("text", event.target.id);
}

function newElement(event) {
    event.preventDefault();
    var data = event.dataTransfer.getData("text");
    console.log(data)
    var parentClass = document.getElementById(data).parentNode.className;
    //console.log(parentClass)
    var element = document.getElementById(data)
    if (parentClass === "dragItemContainer") {
        let type= data.substring(0,4)
        let strModal=null
        switch (type) {
            case "Fuen": strModal= "data-target=\"#modalEditFuente\""
                break
            case "Cola": strModal= "data-target=\"#modalEditCola\""
                break
            case "Proc": strModal= "data-target=\"#modalEditProc\""
                break
            case "Sumi": strModal= "data-target=\"#modalEditSumidero\""
                break
        }
        element = document.getElementById(data).cloneNode(true)
        element.draggable = true;
        element.addEventListener("dragstart", drag);
        element.style.position = "absolute";
        element.style.left = event.pageX - document.getElementById(data).offsetWidth*1.5 + "px";
        element.style.top = event.pageY - document.getElementById(data).offsetHeight*1.5 + "px";
        element.id = "Elemento" + contador
        contador = contador + 1
        var headerButtons= document.createElement("div")
        headerButtons.innerHTML="<div class='buttons'>"+
            "<i type='button' class='actionButton fa-regular fa-pen-to-square' data-toggle=\"modal\""+strModal+"></i>"+
            "<i type='button' class='actionButton fa-regular fa-trash-can' onclick=\"deleteElement2(\'"+element.id+"\')\"></i>"+
            "</div>"
        element.insertBefore(headerButtons,element.firstChild)
        event.target.appendChild(element);
        /*var nuevoElemento = document.createElement();
        nuevoElemento.innerHTML = element2;
        event.target.replaceChild(nuevoElemento,element)*/

    } else {
        element.draggable = true;
        element.addEventListener("dragstart", drag);
        element.style.position = "absolute";
        element.style.left = event.pageX - document.getElementById(data).offsetWidth*1.5 + "px";
        element.style.top = event.pageY - document.getElementById(data).offsetHeight*1.5  + "px";
    }
}

function moveElement(event) {
    var element = event.target;
    console.log(element)
    element.style.left = event.pageX  + "px";
    element.style.top = event.pageY  + "px";
}


//Event Listeners
/*var imageElement = document.getElementById("elemento");
imageElement.addEventListener("dragstart", drag);
imageElement.addEventListener("dragend", moveElement);*/

var destinationElement = document.getElementById("canvas");
destinationElement.addEventListener("dragover", dragOver);
destinationElement.addEventListener("drop", newElement);

var trash = document.getElementById("trash");
trash.addEventListener("dragover", function (event) {
    trash.style.transform = 'scale(1.2)';
    trash.style.color= 'red';
    trash.style.transition= 'color 0.5s';
    dragOver(event)
});
trash.addEventListener("drop", function (event) {
    deleteElement(event);
    trash.style.transform = 'scale(1)';
    trash.style.color= 'black';

});


trash.addEventListener('dragleave', function () {
    trash.style.transform = 'scale(1)';
    trash.style.color= 'black';

});


//-----------
//Inputs logic

//--Source
let inputSource= document.getElementById("numberProducts")
let limitedSource= document.getElementById("limitedSource");
limitedSource.addEventListener("click", function () {
    inputSource.value= "0"
});

let unlimitedSource= document.getElementById("unlimitedSource");
unlimitedSource.addEventListener("click", function () {
    inputSource.value= "Ilimitados"
});


//--Queue
//----Capacity
let inputCapacityQueue= document.getElementById("capacityQueue")
let limitedQueue= document.getElementById("limitedCapacityQueue");
limitedQueue.addEventListener("click", function () {
    inputCapacityQueue.value= "1"
});

let unlimitedQueue= document.getElementById("unlimitedCapacityQueue");
unlimitedQueue.addEventListener("click", function () {
    inputCapacityQueue.value= "Ilimitados"
});
//----Discipline
let inputDisciplineQueue= document.getElementById("queueDiscipline")
let fifoQueue= document.getElementById("fifoQueue");
fifoQueue.addEventListener("click", function () {
    inputDisciplineQueue.value= "Fifo"
});

let lifoQueue= document.getElementById("lifoQueue");
lifoQueue.addEventListener("click", function () {
    inputDisciplineQueue.value= "Lifo"
});

let randomQueue= document.getElementById("randomQueue");
randomQueue.addEventListener("click", function () {
    inputDisciplineQueue.value= "Random"
});


//--Server
let inputCycletimeServer= document.getElementById("cycletimeServer")
let negExpServer= document.getElementById("negExpServer");
negExpServer.addEventListener("click", function () {
    inputCycletimeServer.value= "NegExp(10)"
});

let erlangServer= document.getElementById("erlangServer");
erlangServer.addEventListener("click", function () {
    inputCycletimeServer.value= "Erlang(10,2)"
});

let logNormalServer= document.getElementById("logNormalServer");
logNormalServer.addEventListener("click", function () {
    inputCycletimeServer.value= "LogNormal(10,2)"
});

let bernouilliServer= document.getElementById("bernouilliServer");
bernouilliServer.addEventListener("click", function () {
    inputCycletimeServer.value= "Bernouilli(50,5,15)"
});

let maxNormalServer= document.getElementById("maxNormalServer");
maxNormalServer.addEventListener("click", function () {
    inputCycletimeServer.value= "Max(0,Normal(10,1))"
});

let betaServer= document.getElementById("betaServer");
betaServer.addEventListener("click", function () {
    inputCycletimeServer.value= "Beta(10,1,1)"
});

let gammaServer= document.getElementById("gammaServer");
gammaServer.addEventListener("click", function () {
    inputCycletimeServer.value= "Gamma(10,2)"
});

let maxLogisticServer= document.getElementById("maxLogisticServer");
maxLogisticServer.addEventListener("click", function () {
    inputCycletimeServer.value= "Max(0,Logistic(10,1))"
});

let uniformServer= document.getElementById("uniformServer");
uniformServer.addEventListener("click", function () {
    inputCycletimeServer.value= "Uniform(5,15)"
});

let weibullServer= document.getElementById("weibullServer");
weibullServer.addEventListener("click", function () {
    inputCycletimeServer.value= "Weibull(10,2)"
});

let segServer= document.getElementById("segServer");
segServer.addEventListener("click", function () {
    inputCycletimeServer.value= "10"
});

let minsServer= document.getElementById("minsServer");
minsServer.addEventListener("click", function () {
    inputCycletimeServer.value= "mins(10)"
});

let hrServer= document.getElementById("hrServer");
hrServer.addEventListener("click", function () {
    inputCycletimeServer.value= "hr(10)"
});