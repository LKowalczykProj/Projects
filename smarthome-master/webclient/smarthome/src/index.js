import React from 'react';
import ReactDOM from 'react-dom';
import axios from 'axios';

import {screen} from "./constants";
import AuthScreen from './auth_screen';
import RegistrationScreen from './registration_screen';
import Menu from './menu';
import HomeScreen from './home_screen';
import RoomsScreen from './rooms_screen';
import DevicesScreen from './devices_screen';
import SettingsScreen from './settings_screen';


// axios.defaults.baseURL = 'http://51.38.131.73:8000/api/';
// axios.defaults.baseURL = 'https://73.ip-51-38-131.eu:8001/api/';
axios.defaults.baseURL = 'http://73.ip-51-38-131.eu:8000/';


class App extends React.Component{
    constructor(props) {
        super(props);
        this.state = {
            isLoggedIn: false,
            screen: screen.AUTHORIZATION
        }
        this.handleVerificationSuccess = this.handleVerificationSuccess.bind(this);
        this.handleRegistration = this.handleRegistration.bind(this);
        this.handleGoToAuthScreen = this.handleGoToAuthScreen.bind(this);
        this.handleHome = this.handleHome.bind(this);
        this.handleRooms = this.handleRooms.bind(this);
        this.handleDevices = this.handleDevices.bind(this);
        this.handleSettings = this.handleSettings.bind(this);
        this.handleLogout = this.handleLogout.bind(this);
    }

    handleVerificationSuccess() {
        this.setState({
            isLoggedIn: true,
            screen: screen.HOME
        });
    }

    handleRegistration() {
        this.setState({
            screen: screen.REGISTRATION
        });
    }

    handleGoToAuthScreen() {
        this.setState({
            screen: screen.AUTHORIZATION
        });
    }

    handleHome() {
        this.setState({
            screen: screen.HOME
        });
    }

    handleRooms() {
        this.setState({
            screen: screen.ROOMS
        });
    }

    handleDevices() {
        this.setState({
            screen: screen.DEVICES
        });
    }

    handleSettings() {
        this.setState({
            screen: screen.SETTINGS
        });
    }

    handleLogout() {
        this.setState({
           isLoggedIn: false,
           screen: screen.AUTHORIZATION
        });
        axios.defaults.headers['Authorization'] = '';
    }

    render() {
        const isLoggedIn = this.state.isLoggedIn;
        const currentScreen = this.state.screen;

        if (!isLoggedIn) {
            if(currentScreen === screen.REGISTRATION) {
                return (
                    <RegistrationScreen
                        onGoToAuthScreen={this.handleGoToAuthScreen}
                    />
                );
            } else {
                return (
                    <AuthScreen
                        onVerificationSuccess={this.handleVerificationSuccess}
                        onRegistration={this.handleRegistration}
                    />
                );
            }
        } else {
            let content = "";
            switch (currentScreen) {
                case screen.HOME:
                    content = <HomeScreen/>;
                    break;
                case screen.ROOMS:
                    content = <RoomsScreen/>;
                    break;
                case screen.DEVICES:
                    content = <DevicesScreen/>;
                    break;
                case screen.SETTINGS:
                    content = <SettingsScreen/>;
                    break;
                default:
                    content = <p>Błąd! Ta strona nie powinna istnieć. Proszę o kontakt.</p>;
            }
            return (
                <div>
                    <Menu
                        onHome={this.handleHome}
                        onRooms={this.handleRooms}
                        onDevices={this.handleDevices}
                        onSettings={this.handleSettings}
                        onLogout={this.handleLogout}
                    />
                    {content}
                </div>
            );
        }
    }
}

ReactDOM.render(
    <App />,
    document.getElementById('root')
);