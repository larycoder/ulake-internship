import { authApi, userApi } from "http://common.dev.ulake.sontg.net/js/api.js";

const attrs = {
    firstName: {
        val: o => o.match(/.{1,64}/),
        msg: "First name must be from 1 to 64 characters."
    },
    lastName: {
        val: o => o.match(/.{1,64}/),
        msg: "Last name must be from 1 to 64 characters."
    },
    userName: {
        val: o => o.match(/(\d\w\.)*/),
        msg: "User name must contain only digits and letters."
    },
    password: {
        val: o => o.match(/.{1,64}/),
        msg: "Password must be from 1 to 64 characters."
    },
    email: {
        val: o => o.match(/^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/),
        msg: "Invalid email address."
    }
}

function showError(msg) {
    $(".text-danger").text(msg);
}

function createEntity() {
    const user = {};
    for (const attrName in attrs) {
        const value = $(`#${attrName}`).val();
        if (!attrs[attrName].val(value)) {
            showError(attrs[attrName].msg);
            return;
        }
        user[attrName] = value;
    }

    if (user.password !== $("#password2").val()) {
        showError("Password and Repeat Password do not match.");
        return;
    }
    return user;
}

window.register = async function () {
    // loading...
    const button = $("form a[class*='btn btn-primary']").first();
    button.text("").append($(`<i class="fas fa-spinner fa-spin"></i>`));

    // go!
    const entity = createEntity();
    const data = await userApi.create(entity);
    if (data && Object.keys(data).length > 0) {
        console.log(`Register ok, token=${data}`);
        window.location = "/registered";
    }
    else {
        showError("Cannot register at the moment.");
        button.text("Register Account");
    }
}