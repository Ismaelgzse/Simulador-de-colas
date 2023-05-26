const toggleAnswer = document.querySelector("#toggleAnswer");
const answer = document.querySelector("#answer");
const icon= document.querySelector("#icon");

toggleAnswer.addEventListener("click", function () {

    // toggle the type attribute
    const type = answer.getAttribute("type") === "password" ? "text" : "password";
    answer.setAttribute("type", type);
    // toggle the eye icon
    icon.classList.toggle('fa-eye');
    icon.classList.toggle('fa-eye-slash');
});
