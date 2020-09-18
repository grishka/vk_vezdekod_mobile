import React from 'react';
import '@vkontakte/vkui/dist/vkui.css';
import {Button, Panel, PanelHeader} from "@vkontakte/vkui";
import {getState} from "../state";

const Start = ({id, go}) => {

    const goNext = () => {
        getState(true);
        go("newPodcast");
    };

    return (
        <Panel id={id}>
            <PanelHeader>Создать пост</PanelHeader>

        </Panel>
    );
}

export default Start;
