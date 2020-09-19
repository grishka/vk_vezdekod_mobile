import React from 'react';
import {emotions} from "../state";

const Topic = ({topic, click}) => {
    return (
        <div onClick={click} style={{display: 'flex', flexDirection: 'column', position: 'relative', marginLeft: 12}}>
            <div className="topic-emotion">{emotions[topic.emotion].icon}</div>
            <div className="topic-main">{topic.icon}</div>
            <div className="topic-text">{topic.name}</div>
        </div>
    );
}

export default Topic;
