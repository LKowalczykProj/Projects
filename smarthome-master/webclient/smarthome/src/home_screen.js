import React from "react";
import axios from "axios";

import {Screen, Section, LeftColumn, CenterColumn, RightColumn} from './components';


class HomeScreen extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            rooms: {},
            content: true,
            averageTemperature: 21,
            averageHumidity: 50
        };
        this.render = this.render.bind(this);
        this.forceUpdate = this.forceUpdate.bind(this);
        this.handleNoContent = this.handleNoContent.bind(this);
    }

    componentDidMount() {
        axios.get('api/Room/')
            .then((response) => {
                let rooms = {};
                let temperatureSum = 0;
                let humiditySum = 0;
                response.data.forEach((room) => {
                    rooms[room.id] = room.name;
                    temperatureSum += parseFloat(room.temperature);
                    humiditySum += parseFloat(room.humidity);
                });
                const quantity = response.data.length;
                const averageTemperature = (temperatureSum / quantity).toFixed(1);
                const averageHumidity = (humiditySum / quantity).toFixed(1);
                this.setState({
                    rooms: rooms,
                    averageTemperature: averageTemperature,
                    averageHumidity: averageHumidity
                });
            }).catch((error) => {
            console.log(error.message);
        });
    }

    handleNoContent() {
        this.setState({
            content: false
        });
    }

    render() {
        return (
            <Screen>
                <RightColumn temperature={this.state.averageTemperature} humidity={this.state.averageHumidity} />
                <LeftColumn reload={this.forceUpdate} />
                <CenterColumn>
                    <Section name='Ulubione pomieszczenia' showSectionHeader={false} API='api/FavRoom/' onNoContent={this.state.content ? null : this.handleNoContent} rooms={this.state.rooms} />
                    <Section name='Ulubione oświetlenie' showSectionHeader={false} API='api/FavLamp/' onNoContent={this.state.content ? null : this.handleNoContent} rooms={this.state.rooms} />
                    <Section name='Ulubione urządzenia RTV' showSectionHeader={false} API='api/FavRTV/' onNoContent={this.state.content ? null : this.handleNoContent} rooms={this.state.rooms} />
                    <Section name='Ulubione drzwi' showSectionHeader={false} API='api/FavDoor/' onNoContent={this.state.content ? null : this.handleNoContent} rooms={this.state.rooms} />
                </CenterColumn>
            </Screen>
        );
    }
}

export default HomeScreen;