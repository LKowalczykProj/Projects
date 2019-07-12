import React from "react";
import axios from "axios";

import {Screen, Section} from './components';


class DevicesScreen extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            rooms: {}
        };
    }

    componentDidMount() {
        axios.get('api/Room/')
            .then((response) => {
                let rooms = {};
                response.data.forEach((room) => {
                    rooms[room.id] = room.name;
                });
                this.setState({
                    rooms: rooms
                });
            }).catch((error) => {
                console.log(error.message);
        });
    }

    render() {
        return (
            <Screen>
                <Section name='Oświetlenie' showSectionHeader={true} API='api/Lamp/' rooms={this.state.rooms} />
                <Section name='Urządzenia RTV' showSectionHeader={true} API='api/RTV/' rooms={this.state.rooms} />
                <Section name='Drzwi' showSectionHeader={true} API='api/Door/' rooms={this.state.rooms} />
            </Screen>
        );
    }
}

export default DevicesScreen;