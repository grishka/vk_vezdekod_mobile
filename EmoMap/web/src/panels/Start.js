import React from 'react';
import {Button, Panel, PanelHeader} from "@vkontakte/vkui";

const Start = ({id, go}) => {

    const goNext = () => {
        go("feed");
    };

    return (
        <Panel id={id}>
            <PanelHeader>Создать пост</PanelHeader>

        </Panel>
    );
}

export default Start;
