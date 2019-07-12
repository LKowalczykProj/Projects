import React from "react";

import { Screen } from "./components";
import { AutoMode, TemperatureChanging } from "./utilities";


function SettingsScreen(props) {
    return (
        <Screen>
            <AutoMode />
            <TemperatureChanging />
        </Screen>
    );
}


export default SettingsScreen;