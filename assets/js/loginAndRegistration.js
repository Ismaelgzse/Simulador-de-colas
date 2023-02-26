const togglePassword = document.querySelector("#togglePassword");
const password = document.querySelector("#password");
const icon= document.querySelector("#icon");

togglePassword.addEventListener("click", function () {

    // toggle the type attribute
    const type = password.getAttribute("type") === "password" ? "text" : "password";
    password.setAttribute("type", type);
    // toggle the eye icon
    icon.classList.toggle('fa-eye');
    icon.classList.toggle('fa-eye-slash');
});

$(function() {
    $('#datepicker').datepicker({
        format: 'dd/mm/yyyy'
    }
    );
});
