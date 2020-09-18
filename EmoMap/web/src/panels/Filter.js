import React from 'react';
import {Button, Panel, PanelHeader} from "@vkontakte/vkui";
import {topics} from "../state";

const Filter = ({id, topicId}) => {
    return (
        <Panel id={id}>
            <PanelHeader>{topics[topicId].name}</PanelHeader>

        </Panel>
    );
}

export default Filter;
