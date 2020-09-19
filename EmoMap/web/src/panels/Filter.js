import React from 'react';
import {PanelHeaderButton, Panel, PanelHeader, IOS, platform} from "@vkontakte/vkui";
import {posts, topics} from "../state";
import Icon28ChevronBack from "@vkontakte/icons/dist/28/chevron_back";
import Icon24Back from "@vkontakte/icons/dist/24/back";
import Post from "../components/Post";

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
            {posts.filter((p) => p.topic === topicId).map((p, i) => <Post data={p} key={i}/> )}
            <div style={{width: '100%', height: 8}}/>
        </Panel>
    );
}

export default Filter;
