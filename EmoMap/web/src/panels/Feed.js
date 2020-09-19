import React from 'react';
import {Button, Panel, PanelHeader} from "@vkontakte/vkui";
import Icon24Globe from '@vkontakte/icons/dist/24/globe';
import {posts} from "../state";
import Post from "../components/Post";

const Feed = ({id, go}) => {

    const goNext = () => {
        go("map");
    };

    return (
        <Panel id={id}>
            <PanelHeader>Новости</PanelHeader>
            {posts.map((p, i) => <Post data={p} key={i}/> )}
            <div style={{width: '100%', height: 8}}/>
            <Button className="globe-btn" onClick={goNext}><Icon24Globe/></Button>
        </Panel>
    );
}

export default Feed;
