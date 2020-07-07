import Foundation

public final class ShareStore {

    public static let store = ShareStore()
    private init() {
        self.text = ""
        self.url = ""
        self.image = ""
        self.processed = false
    }

    var text: String;
    var url: String;
    var image: String;
    var processed: Bool;
}
