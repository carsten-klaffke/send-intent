import { IonContent, IonHeader, IonPage, IonTitle, IonToolbar } from '@ionic/react';
import React from 'react';
import ExploreContainer from '../components/ExploreContainer';
import './Home.css';

import { SendIntent } from 'send-intent';

const Home: React.FC = () => {

    function checkIntent() {
        SendIntent.checkSendIntentReceived().then((result: any) => (async function (result: any) {
            alert(result.url)
            alert(result.text)
            alert(result.image)
        })(result)).catch((err:any) => console.log(err))
    }

    window.addEventListener("sendIntentReceived", () => {
        checkIntent();
    })

    checkIntent();

    return (
    <IonPage>
      <IonHeader>
        <IonToolbar>
          <IonTitle>Blank</IonTitle>
        </IonToolbar>
      </IonHeader>
      <IonContent>
        <IonHeader collapse="condense">
          <IonToolbar>
            <IonTitle size="large">Blank</IonTitle>
          </IonToolbar>
        </IonHeader>
        <ExploreContainer />
      </IonContent>
    </IonPage>
  );
};

export default Home;
