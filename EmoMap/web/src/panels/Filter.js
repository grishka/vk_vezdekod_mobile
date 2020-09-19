import React from 'react';
import {PanelHeaderButton, Panel, PanelHeader, IOS, platform} from "@vkontakte/vkui";
import {topics} from "../state";
import Icon28ChevronBack from "@vkontakte/icons/dist/28/chevron_back";
import Icon24Back from "@vkontakte/icons/dist/24/back";

const osName = platform();

const Filter = ({id, topicId}) => {
    return (
        <Panel id={id}>
            <PanelHeader
                left={<PanelHeaderButton onClick={() => window.history.back()}>
                    {osName === IOS ? <Icon28ChevronBack/> : <Icon24Back/>}
                </PanelHeaderButton>}
            >
                {topics[topicId].name}
            </PanelHeader>
        </Panel>
    );
}

export default Filter;
