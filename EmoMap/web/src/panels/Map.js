import React from 'react';
import {Button, Panel, PanelHeader} from "@vkontakte/vkui";

const Map = ({id, go, setTopicId}) => {

    const goNext = (topicId) => {
        setTopicId(topicId);
        go("filter");
    };

    return (
        <Panel id={id}>

        </Panel>
    );
}

export default Map;
