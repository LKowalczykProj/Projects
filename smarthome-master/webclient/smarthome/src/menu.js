import React from "react";


class Menu extends React.Component {
    constructor(props) {
        super(props);
        this.handleHome = this.handleHome.bind(this);
        this.handleRooms = this.handleRooms.bind(this);
        this.handleDevices = this.handleDevices.bind(this);
        this.handleSettings = this.handleSettings.bind(this);
        this.handleLogout = this.handleLogout.bind(this);
        this.handleMore = this.handleMore.bind(this);
    }

    handleHome() {
        this.props.onHome();
    }

    handleRooms() {
        this.props.onRooms();
    }

    handleDevices() {
        this.props.onDevices();
    }

    handleSettings() {
        this.props.onSettings();
    }

    handleLogout() {
        this.props.onLogout();
    }

    handleMore() {
        const topnav = document.getElementById("topnav");
        if (topnav.className === "topnav")
            topnav.className += " responsive";
        else
            topnav.className = "topnav";
    }

    render() {
        return (
            <div className="topnav" id="topnav">
                <ul>
                    <li><button type="button" onClick={this.handleHome} className="tp-icon">
                        <i className="fa fa-home"></i>
                    </button></li>
                    <li><button type="button" onClick={this.handleRooms}>Pokoje</button></li>
                    <li><button type="button" onClick={this.handleDevices}>Urządzenia</button></li>
                    <li><button type="button" onClick={this.handleSettings}>Ustawienia</button></li>
                    <li><button type="button" onClick={this.handleLogout}>Wyloguj</button></li>
                    <li className="more"><button type="button" onClick={this.handleMore} className="tp-icon">
                        <i className="fa fa-bars"></i>
                    </button></li>
                </ul>
            </div>
        )
    }
}

export default Menu;