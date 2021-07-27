import UIKit
import Social
import MobileCoreServices

class ShareViewController: SLComposeServiceViewController {

    private var urlString: String?
    private var textString: String?
    private var imageString: String?
    private var fileString: String?

    override func isContentValid() -> Bool {
        // Do validation of contentText and/or NSExtensionContext attachments here
        print(contentText ?? "content is empty")
        return true
    }

    override func didSelectPost() {
        var urlString = "SendIntentExample://?text=" + (self.textString ?? "");
        urlString = urlString + "&url=" + (self.urlString ?? "");
        urlString = urlString + "&image=" + (self.imageString ?? "");
        urlString = urlString + "&file=" + (self.fileString?.addingPercentEncoding(withAllowedCharacters: .urlHostAllowed) ?? "");
        let url = URL(string: urlString)!
        openURL(url)
        self.extensionContext!.completeRequest(returningItems: [], completionHandler: nil)
    }

    override func configurationItems() -> [Any]! {
        // To add configuration options via table cells at the bottom of the sheet, return an array of SLComposeSheetConfigurationItem here.
        return []
    }

    override func viewDidLoad() {
      super.viewDidLoad()

      let extensionItem = extensionContext?.inputItems[0] as! NSExtensionItem
      let contentTypeURL = kUTTypeURL as String
      let contentTypeText = kUTTypeText as String

      for attachment in extensionItem.attachments as! [NSItemProvider] {

          attachment.loadItem(forTypeIdentifier: contentTypeURL, options: nil, completionHandler: { (results, error) in
                if results != nil {
                let url = results as! URL?
                if url!.isFileURL {
                    do {
                        self.fileString = try! String(contentsOf: url!, encoding: .utf8)
                    }
                } else {
                    self.urlString = url!.absoluteString
                }
            }
          })

          attachment.loadItem(forTypeIdentifier: contentTypeText, options: nil, completionHandler: { (results, error) in
            if results != nil {
                let text = results as! String
                self.textString = text
                _ = self.isContentValid()
            }
          })

      }
    }

    @objc func openURL(_ url: URL) -> Bool {
        var responder: UIResponder? = self
        while responder != nil {
            if let application = responder as? UIApplication {
                return application.perform(#selector(openURL(_:)), with: url) != nil
            }
            responder = responder?.next
        }
        return false
    }

}
