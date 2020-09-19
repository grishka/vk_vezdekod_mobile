import React,{useState, useEffect} from 'react';
import {Button, Panel, PanelHeader, Div, Header, ActionSheet, ActionSheetItem} from "@vkontakte/vkui";
import Icon16Dropdown from '@vkontakte/icons/dist/16/dropdown';
import Icon24Done from '@vkontakte/icons/dist/24/done';
import {emotions, posts, topics} from "../state";

const Start = ({id, go, setPopout}) => {

    const [wallText, setWallText] = useState("");
    const [topicId, setTopicId] = useState(-1);
    const [emotionId, setEmotionId] = useState(-1);

    const goNext = () => {
        if (topicId >= 0 && emotionId >= 0 && wallText) {
            // fake coordinates
            const p = posts.filter((x) => x.topic === topicId);
            const p1 = p[0];
            const p2 = p[p.length - 1];
            const lat = (p1.lat + p2.lat) / 2;
            const lng = (p1.lng + p2.lng) / 2;
            //
            posts.unshift({
                avatar: "http://placeimg.com/50/50/people?r=0",
                name: "Это Вы",
                image: "",
                text: wallText,
                topic: topicId,
                emotion: emotionId,
                lat,
                lng,
            });
            go("feed");
        }
    };

    const openTopics = () => {
        setPopout(<ActionSheet onClose={() => setPopout(null)}>
            {topics.map((t, i) => <ActionSheetItem onClick={() => setTopicId(i)} autoclose key={i}>{t.icon} {t.name}</ActionSheetItem>)}
        </ActionSheet>);
    };

    const openEmotions = () => {
        setPopout(<ActionSheet onClose={() => setPopout(null)}>
            {emotions.map((t, i) => <ActionSheetItem onClick={() => setEmotionId(i)} autoclose key={i}>{t.icon} {t.name}</ActionSheetItem>)}
        </ActionSheet>);
    };

    useEffect(() => {
        const ta = document.getElementById("ta");
        ta.style.height = 'auto';
        ta.style.height = `${ta.scrollHeight}px`;
    }, [wallText]);

    return (
        <Panel id={id} style={{position: 'relative'}}>
            <PanelHeader>Создать пост</PanelHeader>
            <Div style={{height: 200, minHeight: 200, maxHeight: 200, overflowY: 'scroll', overflowX: 'hidden', backgroundColor: 'white'}}>
                <textarea
                    id="ta"
                    value={wallText}
                    onChange={(e) => setWallText(e.currentTarget.value)}
                    className="wall-post-text"
                />
            </Div>
            <Div style={{display: 'flex', backgroundColor: 'white'}}>
                <Button
                    mode="outline"
                    after={<Icon16Dropdown/>}
                    style={{color: (topicId === -1 ? 'var(--text_secondary)' : 'var(--accent)'), borderColor: (topicId === -1 ? 'var(--text_secondary)' : 'var(--accent)'), paddingRight: 8}}
                    onClick={openTopics}
                >
                    {topicId === -1 ? 'Тематика' : topics[topicId].name}
                </Button>
                <Button
                    mode="outline"
                    style={{marginLeft: 8, color: (emotionId === -1 ? 'var(--text_secondary)' : 'var(--accent)'), borderColor: (emotionId === -1 ? 'var(--text_secondary)' : 'var(--accent)'), paddingRight: 8}}
                    after={<Icon16Dropdown/>}
                    onClick={openEmotions}
                >
                    {emotionId === -1 ? 'Настроение' : emotions[emotionId].name}
                </Button>
            </Div>
            <Button
                mode="tertiary"
                className="done-btn"
                style={{opacity: (topicId >= 0 && emotionId >= 0 && wallText ? 1 : 0.5)}}
                onClick={goNext}
            >
                <Icon24Done/>
            </Button>
        </Panel>
    );
}

export default Start;
