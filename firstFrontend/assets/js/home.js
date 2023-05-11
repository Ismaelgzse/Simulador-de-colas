/* Function that moves the main content and the sidenav when a button is triggered*/
sBtn=document.getElementById("mySidenav")
content= document.getElementById("content")
sidenavBtn.addEventListener("click", function () {
    if (sBtn.style.width === '0px' || sBtn.style.width === '') {
        sBtn.style.width = "250px";
        content.style.marginLeft= "250px";
    } else {
        sBtn.style.width = "0";
        content.style.marginLeft= "0px";

    }
});