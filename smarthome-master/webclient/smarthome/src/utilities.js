import React from "react";
import axios from "axios";


class FavouriteButton extends React.Component {
    constructor(props) {
        super(props);
        this.handleClick = this.handleClick.bind(this);
    }

    handleClick() {
        const favourite = !this.props.entity.favourite;
        axios.patch(this.props.entity.API + this.props.entity.id + '/', {
            favourite: favourite
        }).then(() => {
            this.props.reload();
        }).catch((error) => {
            console.log(error.message);
        });
    }

    render() {
        return (
            <button
                type="button"
                onClick={this.handleClick}>
                {this.props.entity.favourite ? <i className="fa fa-star"></i> : <i className="fa fa-star-o"></i>}
            </button>
        );
    }
}

function SettingsButton (props) {
    return (
        <button
            type="button"
            onClick={props.onClick}>
            <i className='fa fa-cog'></i>
        </button>
    );
}

function Header (props) {
    let name = '';
    if (props.name)
        name = props.name;
    else if(props.entity.name)
        name = props.entity.name;
    else
        name = props.rooms[props.entity.room1] + " - " + props.rooms[props.entity.room2];

    return (
        <div className="header">
            <div className="header-content">
                <div className="buttons">
                    {props.children}
                </div>
                <h1>{name}</h1>
            </div>
        </div>
    );
}

function Content (props) {
    const className = "tile item " + getType(props.entity.API);
    return(
        <div className={className}>
            <Header
                entity={props.entity}
                rooms={props.rooms}
            >
                <SettingsButton
                    onClick={props.onSettings}
                />
                <FavouriteButton
                    entity={props.entity}
                    reload={props.reload}
                />
            </Header>

            {typeof props.entity.state != 'undefined' &&
            <State
                entity={props.entity}
                reload={props.reload}
            />
            }

            {typeof props.entity.intensity != 'undefined' && props.entity.dimmable &&
            <Intensity
                entity={props.entity}
                reload={props.reload}
            />
            }

            {typeof props.entity.volume != 'undefined' &&
            <Volume
                entity={props.entity}
                reload={props.reload}
            />
            }

            {typeof props.entity.room != 'undefined' &&
            <Room room={props.entity.room} rooms={props.rooms} />
            }

            {typeof props.entity.temperature != 'undefined'
            && props.entity.temperature !== null &&
            <Temperature temperature={props.entity.temperature} />
            }

            {typeof props.entity.humidity != 'undefined'
            && props.entity.humidity !== null &&
            <Humidity humidity={props.entity.humidity} />
            }

            {typeof props.entity.people != 'undefined'
            && props.entity.people !== null &&
            <People people={props.entity.people} />
            }
        </div>
    );
}

class Settings extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            name: this.props.entity.name,
            room: this.props.entity.room,
            room1: this.props.entity.room1,
            room2: this.props.entity.room2,
            dimmable: this.props.entity.dimmable
        };
        this.handleNameChange = this.handleNameChange.bind(this);
        this.handleRoomChange = this.handleRoomChange.bind(this);
        this.handleRoom1Change = this.handleRoom1Change.bind(this);
        this.handleRoom2Change = this.handleRoom2Change.bind(this);
        this.handleDimmableChange = this.handleDimmableChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleCancel = this.handleCancel.bind(this);
        this.handleDelete = this.handleDelete.bind(this);
    }

    handleNameChange(event) {
        const typedName = event.target.value;
        this.setState({
            name: typedName
        });
    }

    handleRoomChange(event) {
        const selectedRoom = event.target.value;
        this.setState({
            room: selectedRoom
        });
    }

    handleRoom1Change(event) {
        const selectedRoom = event.target.value;
        this.setState({
            room1: selectedRoom
        });
    }

    handleRoom2Change(event) {
        const selectedRoom = event.target.value;
        this.setState({
            room2: selectedRoom
        });
    }

    handleDimmableChange(event) {
        const changedDimmable = !this.state.dimmable;
        this.setState({
            dimmable: changedDimmable
        });
    }

    handleSubmit(event) {
        event.preventDefault();
        let changes = {};
        if (this.state.name !== this.props.entity.name)
            changes["name"] = this.state.name;
        if (this.state.room !== this.props.entity.room)
            changes["room"] = this.state.room;
        if (this.state.room1 !== this.props.entity.room1)
            changes["room1"] = this.state.room1;
        if (this.state.room2 !== this.props.entity.room2)
            changes["room2"] = this.state.room2;
        if (this.state.dimmable !== this.props.entity.dimmable)
            changes["dimmable"] = this.state.dimmable;

        axios.patch(this.props.entity.API + this.props.entity.id + '/', changes)
            .then(() => {
                this.props.reload();
            }).catch((error) => {
                console.log(error.message);
        });
    }

    handleCancel() {
        this.props.onSettings();
    }

    handleDelete() {
        console.log("Deleted");
    }

    render() {
        let roomsOptions = [];
        if(typeof this.props.rooms != 'undefined') {
            roomsOptions = Object.keys(this.props.rooms).map((id) =>
                <option value={id} key={id}>{this.props.rooms[id]}</option>
            )
        }
        const className = "tile item " + getType(this.props.entity.API);

        return(
            <div className={className}>
                <Header
                    entity={this.props.entity}
                    rooms={this.props.rooms}
                >
                    <button type="button" onClick={this.handleDelete}><i className="fa fa-trash-o"></i></button>
                    <button type="button" onClick={this.handleCancel}><i className="fa fa-remove"></i></button>
                    <button type="button" onClick={this.handleSubmit}><i className="fa fa-check"></i></button>
                </Header>
                <form>

                    {typeof this.props.entity.name != 'undefined' &&
                    <div className="parameter double">
                        <p>Nazwa: </p>
                        <input
                            type="text"
                            name="name"
                            value={this.state.name}
                            onChange={this.handleNameChange}
                        />
                    </div>
                    }

                    {typeof this.props.entity.room != 'undefined' &&
                    <div className="parameter double">
                        <p>Pomieszczenie: </p>
                        <select
                            name="room"
                            value={this.state.room}
                            onChange={this.handleRoomChange}
                        >
                            {roomsOptions}
                        </select>
                    </div>
                    }

                    {typeof this.props.entity.room1 != 'undefined' &&
                    <div className="parameter double">
                        <p>Pomieszczenie 1: </p>
                        <select
                            name="room"
                            value={this.state.room1}
                            onChange={this.handleRoom1Change}
                        >
                            {roomsOptions}
                        </select>
                    </div>
                    }

                    {typeof this.props.entity.room2 != 'undefined' &&
                    <div className="parameter double">
                        <p>Pomieszczenie 2: </p>
                        <select
                            name="room"
                            value={this.state.room2}
                            onChange={this.handleRoom2Change}
                        >
                            {roomsOptions}
                        </select>
                    </div>
                    }

                    {typeof this.props.entity.dimmable != 'undefined' &&
                    <div className="parameter">
                        <p>Ściemnialne: </p>
                        <input
                            type="checkbox"
                            name="dimmable"
                            checked={this.state.dimmable}
                            onChange={this.handleDimmableChange}
                        />
                    </div>
                    }
                </form>
            </div>
        );
    }
}

class Actions extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            houseID: null
        };
        this.handleLightsOff = this.handleLightsOff.bind(this);
        this.handleDevicesOff = this.handleDevicesOff.bind(this);
        this.handleDoorClose = this.handleDoorClose.bind(this);
    }

    componentDidMount() {
        axios.get('api/House/')
            .then((response) => {
                const id = response.data[0].id;
                this.setState({
                    houseID: id
                });
            }).catch((error) => {
                console.log(error.message);
        });
    }

    handleLightsOff() {
        axios.get('lightout/' + this.state.houseID)
            .then((response) => {
                console.log(response.data);
                this.props.reload();
            }).catch((error) => {
                console.log(error);
        });
    }

    handleDevicesOff() {
        axios.get('shutdown/' + this.state.houseID)
            .then((response) => {
                console.log(response.data);
            }).catch((error) => {
            console.log(error);
        });
    }

    handleDoorClose() {
        axios.get('lockdown/' + this.state.houseID)
            .then((response) => {
                console.log(response.data);
            }).catch((error) => {
            console.log(error);
        });
    }

    render() {
        return (
            <div className="tile actions">
                <Header name="Akcje" />
                <ul>
                    <li>
                        <ActionButton
                            name="Zgaś wszystkie światła"
                            onClick={this.handleLightsOff}
                        />
                    </li>
                    <li>
                        <ActionButton
                            name="Wyłącz wszystkie urządzenia"
                            onClick={this.handleDevicesOff}
                        />
                    </li>
                    <li>
                        <ActionButton
                            name="Zamknij wszystkie drzwi"
                            onClick={this.handleDoorClose}
                        />
                    </li>
                </ul>
            </div>
        );
    }
}

function Home(props) {
    return (
        <div className="tile">
            <Header name="Dom" />
            <Temperature temperature={props.temperature} />
            <Humidity humidity={props.humidity} />
        </div>
    );
}

class AutoMode extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            houseID: null,
            state: false
        };
        this.loadMode = this.loadMode.bind(this);
    }

    loadMode() {
        axios.get('api/House/')
            .then((response) => {
                const id = response.data[0].id;
                const auto = response.data[0].auto;
                this.setState({
                    houseID: id,
                    state: auto
                });
            }).catch((error) => {
            console.log(error.message);
        });
    }

    componentDidMount() {
        this.loadMode();
    }

    render() {
        return (
            <div className="tile auto">
                <Header name="Tryb automatyczny" />
                <StateAuto
                    state={this.state.state}
                    houseID={this.state.houseID}
                    reload={this.loadMode}
                />
            </div>
        );
    }
}

class TemperatureChanging extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            houseID: null,
            error: false,
            isLoaded: false,
            entities: []
        };
        this.loadHouseID = this.loadHouseID.bind(this);
        this.loadRooms = this.loadRooms.bind(this);
        this.handleTemperatureChange = this.handleTemperatureChange.bind(this);
    }

    loadHouseID() {
        axios.get('api/House/')
            .then((response) => {
                const id = response.data[0].id;
                this.setState({
                    houseID: id
                });
            }).catch((error) => {
            console.log(error.message);
        });
    }

    loadRooms() {
        const allRooms = {
            id: 0,
            name: "Wszystkie",
            temperature: "20.0"
        };
        axios.get("api/Room/")
            .then((response) => {
                this.setState({
                    entities: []
                });
                this.setState({
                    isLoaded: true,
                    entities: response.data
                });
                this.setState({
                    entities: this.state.entities.concat(allRooms)
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
        this.loadHouseID();
        this.loadRooms();
    }

    handleTemperatureChange(event) {
        const temperature = event.target.value;
        const room = parseInt(event.target.name);
        axios.get('tempcontrol/' + this.state.houseID +'/' + temperature + '/' + room)
            .then((response) => {
                console.log(response.data);
            }).catch((error) => {
            console.log(error);
        });
        let newEntities = this.state.entities.slice();
        newEntities.forEach((entity) => {
            if(entity.id === room) {
                entity.temperature = temperature;
            }
        });
        this.setState({
            entities: newEntities
        });
    }

    render() {
        let message = '';
        if(!this.state.isLoaded) {
            message = "<p>Wczytywanie temperatur...</p>)";
        }
        let temperatures = [];
        for (let i = 0; i <= 30; i += 0.5) {
            temperatures.push(<option value={i} key={i*2}>{i}</option>);
        }
        return (
            <div className="tile temperature">
                <Header name="Zmiana temperatury" />
                <form>
                    {message ? message : this.state.entities.map((entity) => (
                        <div className="parameter double" key={entity.id}>
                            <p>{entity.name}: </p>
                            <select
                                name={entity.id}
                                value={parseFloat(entity.temperature)}
                                onChange={this.handleTemperatureChange}
                            >
                                {temperatures}
                            </select>
                        </div>
                    ))
                    }
                </form>
            </div>
        );
    }
}

class ToggleButton extends React.Component {
    constructor(props) {
        super(props);
        this.entity = this.props.entity;
        this.handleClick = this.handleClick.bind(this);
    }

    handleClick() {
        const state = !this.entity.state;
        axios.patch(this.entity.API + this.entity.id + '/', {
            state: state
        }).then(() => {
            this.props.reload();
        }).catch((error) => {
            console.log(error.message);
        });
    }

    render() {
        return (
            <button
                type="button"
                className="toggle-button"
                onClick={this.handleClick}>
                {this.entity.state ? <i className="fa fa-toggle-on"></i> : <i className="fa fa-toggle-off"></i>}
            </button>
        );
    }
}

class ToggleButtonAuto extends React.Component {
    constructor(props) {
        super(props);
        this.handleClick = this.handleClick.bind(this);
    }

    handleClick() {
        let state = "";
        this.props.state ? state = "off" : state = "on";
        axios.get('auto/' + this.props.houseID + '/' + state)
            .then((response) => {
                console.log(response.data);
                this.props.reload();
            }).catch((error) => {
            console.log(error);
        });
    }

    render() {
        return (
            <button
                type="button"
                className="toggle-button"
                onClick={this.handleClick}>
                {this.props.state ? <i className="fa fa-toggle-on"></i> : <i className="fa fa-toggle-off"></i>}
            </button>
        );
    }
}

class RangeField extends React.Component {
    constructor(props) {
        super(props);
        this.entity = this.props.entity;
        this.state = {
            value: this.entity[this.entity.parameter]
        };
        this.handleChange = this.handleChange.bind(this);
    }

    handleChange(e){
        e.preventDefault();
        const newValue = e.target.value;
        const parameter = this.entity.parameter;
        let data = {};
        data[parameter] = newValue;
        axios.patch(this.entity.API + this.entity.id + '/', data)
            .then(() => {
                this.setState({
                    value: newValue
                });
                this.props.reload();
            }).catch((error) => {
            console.log(error.message);
        });
    }

    render() {
        return(
            <form>
                <label>
                    <p>{this.entity.label + ": "}</p>
                    <input
                        type="number"
                        value={this.state.value}
                        onChange={this.handleChange}
                        min="0"
                        max="100"
                    />
                </label>
                <input
                    type="range"
                    value={this.state.value}
                    onChange={this.handleChange}
                    min="0"
                    max="100"
                />
            </form>
        );
    }
}

function ActionButton(props) {
    return(
        <button
            type="button"
            onClick={props.onClick}>
            {props.name}
        </button>
    );
}

function State (props) {
    return (
        <div className="parameter state">
            <ToggleButton
                entity={props.entity}
                reload={props.reload}
            />
            <p>Stan: {props.entity.state ? "włączone" : "wyłączone"}</p>
        </div>
    );
}

function StateAuto (props) {
    return (
        <div className="parameter state">
            <ToggleButtonAuto
                state={props.state}
                houseID={props.houseID}
                reload={props.reload}
            />
            <p>Stan: {props.state ? "włączone" : "wyłączone"}</p>
        </div>
    );
}

function Room (props) {
    return (
        <div className="parameter">
            <p>Pomieszczenie: {props.rooms[props.room] ? props.rooms[props.room] : props.room}</p>
        </div>
    );
}

function Intensity(props) {
    props.entity.label = "Intensywność";
    props.entity.parameter = "intensity";
    return (
        <div className="parameter double">
            <RangeField
                entity={props.entity}
                reload={props.reload}
            />
        </div>
    );
}

function Volume(props) {
    props.entity.label = "Głośność";
    props.entity.parameter = "volume";
    return (
        <div className="parameter double">
            <RangeField
                entity={props.entity}
                reload={props.reload}
            />
        </div>
    );
}

function Temperature (props) {
    return (
        <div className="parameter tight">
            <p>Temperatura: {props.temperature}&#8451;</p>
        </div>
    );
}

function Humidity (props) {
    return (
        <div className="parameter tight">
            <p>Wilgotność: {props.humidity}%</p>
        </div>
    );
}

function People (props) {
    return (
        <div className="parameter tight">
            <p>Ruch w pomieszczeniu: {props.people > 0 ? "tak" : "nie"}</p>
        </div>
    );
}

function getType(API) {
    switch (API) {
        case "api/Lamp/":
            return "lamp";
        case "api/RTV/":
            return "rtv";
        case "api/Door/":
            return "door";
        case "api/Room/":
            return "room";
        default:
            return "";
    }
}


export { Header, Content, Settings, Actions, Home, AutoMode, TemperatureChanging, State, Room, Intensity, Volume, Temperature, Humidity, People };