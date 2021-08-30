export interface Intent {
  text?: string;
  file?: string;
  image?: string;
  url?: string;
}

export interface SendIntentPlugin {
  checkSendIntentReceived(): Promise<Intent>;
}
