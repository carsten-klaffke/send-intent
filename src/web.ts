import { WebPlugin } from '@capacitor/core';
import { SendIntentPlugin } from './definitions';

export class SendIntentWeb extends WebPlugin implements SendIntentPlugin {
  constructor() {
    super({
      name: 'SendIntent',
      platforms: ['web']
    });
  }

  async checkSendIntentReceived(): Promise<{text: string}> {
    return {text: null};
  }

}

const SendIntent = new SendIntentWeb();

export { SendIntent };

import { registerWebPlugin } from '@capacitor/core';
registerWebPlugin(SendIntent);
