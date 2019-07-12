import React from "react";
import axios from "axios";

import { Content, Settings, Actions, Home } from "./utilities";


class Entity extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            settings: false
        };
        this.handleSettings = this.handleSettings.bind(this);
    }

    handleSettings() {
        const settings = !this.state.settings;
        this.setState({
            settings: settings
        });
    }

    render() {
        if(this.state.settings)
            return (
                <Settings
                    entity={this.props.entity}
                    rooms={this.props.rooms}
                    onSettings={this.handleSettings}
                    reload={this.props.reload}
                />
            );
        else
            return (
                <Content
                    entity={this.props.entity}
                    rooms={this.props.rooms}
                    onSettings={this.handleSettings}
                    reload={this.props.reload}
                />
            );
    }
}

class Section extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            error: false,
            isLoaded: false,
            entities: []
        };
        this.loadEntities = this.loadEntities.bind(this);
    }

    loadEntities() {
        axios.get(this.props.API)
            .then((response) => {
                this.setState({
                    entities: []
                });
                this.setState({
                    isLoaded: true,
                    entities: response.data
                });
            }).catch((error) => {
                this.setState({
                    isLoaded: true,
                    error: true
                });
                console.log(error.message);
        });
    }

    componentDidMount() {
        this.loadEntities();
    }

    render() {
        let message = '';
        if(!this.state.isLoaded) {
            message = (
                <div className="tile item message">
                    <p>Wczytywanie urządzeń...</p>
                </div>
            );
        } else if (this.state.error) {
            message = (
                <div className="tile item message">
                    <p>Nie można wczytać urządzeń!</p>
                </div>
            );
        } else if (!this.state.entities.length) {
            if (this.props.onNoContent) {
                this.props.onNoContent();
            }
            // return null;
            message = (
                <div className="tile item message">
                    <p>Brak urządzeń</p>
                </div>
            );
        } else {
            this.state.entities.forEach((entity) => {
                entity.API = this.props.API;
            });
        }
        return (
            <div className="section">
                {this.props.showSectionHeader ? <h1>{this.props.name}</h1> : null}
                {message ? message : this.state.entities.map((entity) => (
                    <Entity
                        entity={entity}
                        rooms={this.props.rooms}
                        key={entity.id}
                        reload={this.loadEntities}
                    />
                    ))
                }
            </div>
        );
    }
}

function Empty(props) {
    return (
        <div className="section">
            <div className="empty"></div>
        </div>
    );
}

function LeftColumn(props) {
    return (
        <div className="column-left">
            <Actions reload={props.reload} />
        </div>
    );
}

function CenterColumn(props) {
    return (
        <div className="column-center">
            {props.children}
        </div>
    );
}

function RightColumn(props) {
    return (
        <div className="column-right">
            <Home temperature={props.temperature} humidity={props.humidity} />
        </div>
    );
}

function Screen(props) {
    return (
        <div className="column">
            {props.children}
        </div>
    );
}


export { Entity, Section, Screen, Empty, LeftColumn, CenterColumn, RightColumn };