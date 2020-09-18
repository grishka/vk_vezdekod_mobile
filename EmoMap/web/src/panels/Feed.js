import React from 'react';
import {Button, Panel, PanelHeader} from "@vkontakte/vkui";

const Feed = ({id, go}) => {

    const goNext = () => {
        go("map");
    };

    return (
        <Panel id={id}>
            <PanelHeader>Новости</PanelHeader>

        </Panel>
    );
}

export default Feed;
