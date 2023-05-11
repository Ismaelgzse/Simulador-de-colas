const togglePass1 = document.querySelector("#togglePassword1");
const password1 = document.querySelector("#password1");
const iconPass1= document.querySelector("#iconPass1");

const togglePass2 = document.querySelector("#togglePassword2");
const password2 = document.querySelector("#password2");
const iconPass2= document.querySelector("#iconPass2");

togglePass1.addEventListener("click", function () {

    // toggle the type attribute
    const type = password1.getAttribute("type") === "password" ? "text" : "password";
    password1.setAttribute("type", type);
    // toggle the eye icon
    iconPass1.classList.toggle('fa-eye');
    iconPass1.classList.toggle('fa-eye-slash');
});

togglePass2.addEventListener("click", function () {

    // toggle the type attribute
    const type = password2.getAttribute("type") === "password" ? "text" : "password";
    password2.setAttribute("type", type);
    // toggle the eye icon
    iconPass2.classList.toggle('fa-eye');
    iconPass2.classList.toggle('fa-eye-slash');
});