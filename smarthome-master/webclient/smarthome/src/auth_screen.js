import React from "react";
import axios from "axios";

import {Header} from "./utilities";


class AuthScreen extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            username: '',
            password: '',
            error: ''
        };
        this.handleUsernameChange = this.handleUsernameChange.bind(this);
        this.handlePasswordChange = this.handlePasswordChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleVerificationSuccess = this.handleVerificationSuccess.bind(this);
        this.handleRegistration = this.handleRegistration.bind(this);

    }

    handleUsernameChange(event) {
        const typedName = event.target.value;
        this.setState({
            username: typedName,
            error: ''
        });
    }

    handlePasswordChange(e) {
        const typedPasswd = e.target.value;
        this.setState({
            password: typedPasswd,
            error: ''
        });
    }

    handleSubmit(event) {
        event.preventDefault();
        const uname = this.state.username;
        const passwd = this.state.password;

        axios.post('api/token-auth/', {
            username: uname,
            password: passwd
        }).then((response) => {
            const token = response.data.token;
            axios.defaults.headers['Authorization'] = 'Token ' + token;
            this.handleVerificationSuccess();
        }).catch((error) => {
            console.log(error.message);
            const err = <p className="error">Podano błędny login lub hasło!</p>;
            this.setState({error: err});
        });
    }

    handleVerificationSuccess() {
        this.props.onVerificationSuccess();
    }

    handleRegistration() {
        this.props.onRegistration();
    }

    render() {
        return (
            <div className="column">
                <div className="tile auth">
                    <Header name="Logowanie" />
                    <form className="auth-form" onSubmit={this.handleSubmit}>
                        <label>
                            Nazwa użytkownika:
                            <input
                                type="text"
                                name="username"
                                value={this.state.username}
                                onChange={this.handleUsernameChange} />
                            {this.state.error}
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
                        <input type="submit" value="Zaloguj się" />
                    </form>
                    <button type="button" onClick={this.handleRegistration}>Rejestracja</button>
                </div>
            </div>
        );
    }
}

export default AuthScreen;