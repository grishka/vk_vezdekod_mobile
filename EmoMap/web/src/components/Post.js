import React from 'react';
import {Div, SimpleCell, Avatar, Separator} from "@vkontakte/vkui";
import Icon20LikeOutline from '@vkontakte/icons/dist/20/like_outline';
import Icon20CommentOutline from '@vkontakte/icons/dist/20/comment_outline';
import Icon20ShareOutline from '@vkontakte/icons/dist/20/share_outline';

const Post = ({data}) => {
    return <div className="wall-post">
        <SimpleCell description="Только что" before={<Avatar size={40} src={data.avatar} />}>{data.name}</SimpleCell>
        {data.text && <Div>{data.text}</Div>}
        {data.image && <div style={{width: '100%', minHeight: '60vw'}}>
            <img style={{width: '100%', verticalAlign: 'middle'}} src={data.image}/>
        </div>}
        <Separator/>
        <Div style={{display: 'flex'}}>
            <Icon20LikeOutline style={{color: 'var(--content_placeholder_icon)', margin: 4}}/>
            <Icon20CommentOutline style={{color: 'var(--content_placeholder_icon)', margin: 4, marginLeft: 16}}/>
            <Icon20ShareOutline style={{color: 'var(--content_placeholder_icon)', margin: 4, marginLeft: 16}}/>
        </Div>
    </div>;
};

export default Post;
