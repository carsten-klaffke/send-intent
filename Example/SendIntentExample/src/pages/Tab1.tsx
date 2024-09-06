import {IonContent, IonHeader, IonPage, IonTitle, IonToolbar} from '@ionic/react';
import ExploreContainer from '../components/ExploreContainer';
import './Tab1.css';
import {SendIntent} from "send-intent";
import {Filesystem} from '@capacitor/filesystem';
import {RouteComponentProps} from "react-router";
import React, {useEffect, useState} from "react";


const Tab1: React.FC<RouteComponentProps> = ({history, location, match}) => {

    const [result, setResult] = useState('');

    async function checkIntent() {
        console.log("check intent")
        return SendIntent.checkSendIntentReceived().then(async (result: any) => {
                console.log("checked")
                console.log(result)
                if (result) {
                    console.log('SendIntent received');
                    let res = JSON.stringify(result);
                    console.log(res);
                    setResult(res)

                    //SendIntent.finish();
                }
            }
        ).catch(err => console.error(err));
    }

    checkIntent();

    useEffect(() => {
        console.log("adding listener")
        //Check-Intent: Es werden beide Varianten benötigt, mit und ohne Listener. Denn je nachdem wie das share
        //angesteuert wird lädt die App neu (normaler Aufruf) oder nicht (Listener)
        window.addEventListener("sendIntentReceived", () => {
            history.push("/tab1")
            checkIntent();
        });

        checkIntent();
    }, [])

    return (
        <IonPage>
            <IonHeader>
                <IonToolbar>
                    <IonTitle>Tab 1</IonTitle>
                </IonToolbar>
            </IonHeader>
            <IonContent fullscreen>
                <IonHeader collapse="condense">
                    <IonToolbar>
                        <IonTitle size="large">Share object</IonTitle>
                    </IonToolbar>
                </IonHeader>
                <div>
                    {result}
                </div>
            </IonContent>
        </IonPage>
    );
};

export default Tab1;
