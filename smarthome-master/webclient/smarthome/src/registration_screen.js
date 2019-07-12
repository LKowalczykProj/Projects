import React from "react";
import axios from "axios";

import {Header} from "./utilities";
import {registrationError} from "./constants";


class RegistrationScreen extends React.Component {
    constructor(props) {
        super(props);
        this.error = {

        }
        this.state = {
            username: '',
            fname: '',
            lname: '',
            email: '',
            password: '',
            cpassword: '',
            error: 0
        };
        this.handleUsernameChange = this.handleUsernameChange.bind(this);
        this.handleEmailChange = this.handleEmailChange.bind(this);
        this.handlePasswordChange = this.handlePasswordChange.bind(this);
        this.handleCPasswordChange = this.handleCPasswordChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleGoToAuthScreen = this.handleGoToAuthScreen.bind(this);
    }

    handleUsernameChange(event) {
        const typedName = event.target.value;
        this.setState({
            username: typedName,
            error: ''
        });
    }

    handleEmailChange(event) {
        const typedEmail = event.target.value;
        this.setState({
            email: typedEmail,
            error: ''
        });
    }

    handlePasswordChange(event) {
        const typedPasswd = event.target.value;
        this.setState({
            password: typedPasswd,
            error: ''
        });
    }

    handleCPasswordChange(event) {
        const typedPasswd = event.target.value;
        this.setState({
            cpassword: typedPasswd,
            error: ''
        });
    }

    handleSubmit(event) {
        event.preventDefault();
        const uname = this.state.username;
        const email = this.state.email;
        const passwd = this.state.password;
        const cpasswd = this.state.cpassword;

        if (passwd !== cpasswd) {
            this.setState({
                password: "",
                cpassword: "",
                error: registrationError.PASSWORDS_DIFFER
            });
        } else {
            axios.post('register/', {
                username: uname,
                password: passwd,
                email: email,
            }).then(() => {
                this.handleGoToAuthScreen();
            }).catch((error) => {
                console.log(error.message);
                this.setState({
                    error: registrationError.USERNAME_OCCUPIED
                });
            });
        }
    }

    handleGoToAuthScreen() {
        this.props.onGoToAuthScreen();
    }

    render() {
        return (
            <div className="column">
                <div className="tile auth">
                    <Header name="Rejestracja" />
                    <form className="auth-form" onSubmit={this.handleSubmit}>
                        <label>
                            Nazwa użytkownika:
                            <input
                                type="text"
                                name="username"
                                value={this.state.username}
                                onChange={this.handleUsernameChange} />
                        </label>
                        {this.state.error === registrationError.USERNAME_OCCUPIED &&
                            <p className="error">Podana nazwa użytkownika lub adres email są już zajęte</p>
                        }
                        <label>
                            Adres email:
                            <input
                                type="email"
                                name="email"
                                value={this.state.email}
                                onChange={this.handleEmailChange} />
                        </label>
                        <label>
                            Hasło:
                            <input
                                type="password"
                                name="password"
                                value={this.state.password}
                                onChange={this.handlePasswordChange}
                            />
                        </label>
                        <label>
                            Powtórz hasło:
                            <input
                                type="password"
                                name="cpassword"
                                value={this.state.cpassword}
                                onChange={this.handleCPasswordChange}
                            />
                        </label>
                        {this.state.error === registrationError.PASSWORDS_DIFFER &&
                            <p className="error">Podane hasła różnią się!</p>
                        }
                        <input type="submit" value="Zarejestruj się" />
                    </form>
                    <button type="button" onClick={this.handleGoToAuthScreen}>Powrót</button>
                </div>
            </div>
        );
    }
}

export default RegistrationScreen;