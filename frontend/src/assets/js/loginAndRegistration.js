const togglePassword = document.querySelector("#togglePassword");
const toggleAnswer = document.querySelector("#toggleAnswer");

const password = document.querySelector("#password");
const answer = document.querySelector("#answer");

const icon= document.querySelector("#icon");
const icon2= document.querySelector("#icon2");

togglePassword.addEventListener("click", function () {

    // toggle the type attribute
    const type = password.getAttribute("type") === "password" ? "text" : "password";
    password.setAttribute("type", type);
    // toggle the eye icon
    icon.classList.toggle('fa-eye');
    icon.classList.toggle('fa-eye-slash');
});

toggleAnswer.addEventListener("click", function () {

    // toggle the type attribute
    const type = answer.getAttribute("type") === "password" ? "text" : "password";
    answer.setAttribute("type", type);
    // toggle the eye icon
    icon2.classList.toggle('fa-eye');
    icon2.classList.toggle('fa-eye-slash');
});

$(function() {
    $('#datepicker').datepicker({
        format: 'dd/mm/yyyy'
    }
    );
});
