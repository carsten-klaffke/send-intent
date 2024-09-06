import {WebPlugin} from '@capacitor/core';
import {SendIntentPlugin} from './definitions';

export class SendIntentWeb extends WebPlugin implements SendIntentPlugin {
    constructor() {
        super();
    }

    async checkSendIntentReceived(): Promise<{ title: string }> {
        return {title: ''};
    }

    finish(): void {
    }

}
