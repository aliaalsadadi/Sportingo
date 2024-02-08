const urlParams = new URLSearchParams(window.location.search);
const username = urlParams.get('email');
const password = urlParams.get('password');



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
var db = firebase.firestore();

// Authenticate the user with Firebase
firebase.auth().signInWithEmailAndPassword(username, password)    

    .then(function(userCredential) {
        // Authentication successful
        console.log("Login successful!");
    }).then(function() {
        const user_DisplayName = firebase.auth().currentUser.displayName;
        const user_DisplayEmail = firebase.auth().currentUser.email;
        
        if (user_DisplayName === null) {
            document.getElementById("username_Display").style.fontSize = "8px";
            document.getElementById("username_Display").innerHTML = `${user_DisplayEmail}`;
        }else{
            document.getElementById("username_Display").innerHTML = `${user_DisplayName}`;

            console.log('Display Name:', user_DisplayName);
        }
    })


function firebaseLogout() {
    firebase.auth().signOut()
      .then(() => {
        window.location.href = "../Login_v5/index.html";
})}   

let User_Pushups;
let User_Situps;
let User_User;

async function fetchData() {
  try {
    const querySnapshot = await db.collection("users").get();
    
    querySnapshot.forEach(function(doc) {
      const user_DisplayEmail = firebase.auth().currentUser.email;
      // doc.id is the email address in the Database
      if (user_DisplayEmail === doc.id) {
        User_Pushups = doc.data().pushups;
        User_Situps = doc.data().situps;
        User_User = doc.data().user;
        console.log(User_Pushups, User_Situps, User_User);
      }
    });
    if(User_Pushups = "undefined"){
        document.getElementById("pushups_Counter").innerHTML = "0";
    }else{
        document.getElementById("pushups_Counter").innerHTML = `${User_Pushups}`;
    };
    if(User_Situps = "undefined"){
        document.getElementById("situps_Counter").innerHTML = "0";
    }else{
        document.getElementById("situps_Counter").innerHTML = `${User_Situps}`;
    }
  } catch (error) {
    console.log("Error fetching data: ", error);
  }
}

fetchData();