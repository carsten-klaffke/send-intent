import { WebPlugin } from '@capacitor/core';
import { SendIntentPlugin } from './definitions';

export class SendIntentWeb extends WebPlugin implements SendIntentPlugin {
  constructor() {
    super({
      name: 'SendIntent',
      platforms: ['web']
    });
  }

  async checkSendIntentReceived(): Promise<{title: string}> {
    return {title: null};
  }

}
