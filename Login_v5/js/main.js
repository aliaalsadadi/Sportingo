// Initialize Firebase
var firebaseConfig = {
    apiKey: "AIzaSyA3vEeYHU_EdJvUrAVw191sZYsjX7Gilc8",
    authDomain: "turing-emitter-392118.firebaseapp.com",
    databaseURL: "https://turing-emitter-392118-default-rtdb.firebaseio.com",
    projectId: "turing-emitter-392118",
    storageBucket: "turing-emitter-392118.appspot.com",
    messagingSenderId: "839055180083",
    appId: "1:839055180083:web:273ff9025e25a4e511be46",
    measurementId: "G-DXS22J7HY1"
};
firebase.initializeApp(firebaseConfig);
var database = firebase.database();


(function ($) {
    "use strict";


    /*==================================================================
    [ Validate ]*/
    var input = $('.validate-input .input100');

    $('.validate-form').on('submit',function(){
        var check = true;

        for(var i=0; i<input.length; i++) {
            if(validate(input[i]) == false){
                showValidate(input[i]);
                check=false;
            }
        }

        return check;
    });


    $('.validate-form .input100').each(function(){
        $(this).focus(function(){
           hideValidate(this);
        });
    });

    function validate (input) {
        if($(input).attr('type') == 'email' || $(input).attr('name') == 'email') {
            if($(input).val().trim().match(/^([a-zA-Z0-9_\-\.]+)@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.)|(([a-zA-Z0-9\-]+\.)+))([a-zA-Z]{1,5}|[0-9]{1,3})(\]?)$/) == null) {
                return false;
            }
        }
        else {
            if($(input).val().trim() == ''){
                return false;
            }
        }
    }

    function showValidate(input) {
        var thisAlert = $(input).parent();

        $(thisAlert).addClass('alert-validate');
    }

    function hideValidate(input) {
        var thisAlert = $(input).parent();

        $(thisAlert).removeClass('alert-validate');
    }
    
})(jQuery);

function Firebase_Login (event) {
    event.preventDefault()

    var username = document.getElementById("email").value;
    var password = document.getElementById("password").value;

    // Authenticate the user with Firebase
    firebase.auth().signInWithEmailAndPassword(username, password)
        .then(function(userCredential) {
            // Authentication successful
            document.getElementById("message").innerHTML = "Login successful!";
            window.location.href = `../ltr/index.html?email=${username}&password=${password}`;
        })
        .catch(function(error) {
            // Authentication failed
            document.getElementById("message").innerHTML = "Login failed. Please try again.";
        });
};

function register_User(event) {
    event.preventDefault()

    var username = document.getElementById('signUP_email').value;
    var password = document.getElementById('signUP_password').value;
  
    firebase.auth().createUserWithEmailAndPassword(username, password)
      .then(function(userCredential) {
        // User registered successfully
        document.getElementById("message").innerHTML = "Registered successfully!";
      })
      .catch(function(error) {
        // User registration failed
        var errorCode = error.code;
        var errorMessage = error.message;
        console.error('Registration error:', errorCode, errorMessage);
      });
};

function signUP_Redirect() {
    window.location.href = "signup.html";
};

function signIN_Redirect() {
    window.location.href = "index.html";
};