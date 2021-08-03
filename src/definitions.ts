export interface Intent {
  text?: string;
  image?: string;
  file?: string;
}

export interface SendIntentPlugin {
  checkSendIntentReceived(): Promise<Intent>;
}
