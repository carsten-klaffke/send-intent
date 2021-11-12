export interface Intent {
  title?: string;
  description?: string;
  type?: string;
  url?: string;
  additionalItems?: any;
}

export interface SendIntentPlugin {
  checkSendIntentReceived(): Promise<Intent>;
}
