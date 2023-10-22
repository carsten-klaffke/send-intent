import {Component, NgZone} from '@angular/core';
import { IonHeader, IonToolbar, IonTitle, IonContent } from '@ionic/angular/standalone';
import { ExploreContainerComponent } from '../explore-container/explore-container.component';
import {SendIntent} from "send-intent";

@Component({
  selector: 'app-tab1',
  templateUrl: 'tab1.page.html',
  styleUrls: ['tab1.page.scss'],
  standalone: true,
  imports: [IonHeader, IonToolbar, IonTitle, IonContent, ExploreContainerComponent],
})
export class Tab1Page {

  result: string = '';

  constructor(private zone: NgZone) {}

  ngOnInit() {
    this.checkIntent();

    window.addEventListener('sendIntentReceived', () => {
      this.zone.run(() => {
        console.log('sendIntentReceived event triggered');
        // Hier musst du den Angular Router verwenden, um zur gewünschten Route zu navigieren
        // z.B., this.router.navigate(['/tab1']);
        this.checkIntent();
      });
    });
  }

  async checkIntent() {
    try {
      const result: any = await SendIntent.checkSendIntentReceived();
      if (result) {
        console.log('SendIntent received');
        let res = JSON.stringify(result);
        console.log(res);
        this.result = res;
        // SendIntent.finish();
      }
    } catch (error) {
      console.error(error);
    }
  }
}

