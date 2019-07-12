import React from "react";

import {Screen, Section} from './components';


function RoomsScreen(props) {
    return (
        <Screen>
            <Section name='Pokoje' showSectionHeader={false} API='api/Room/' />
        </Screen>
    );
}


export default RoomsScreen;