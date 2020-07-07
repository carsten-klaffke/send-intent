import Foundation

public final class ShareStore {

    static let store = ShareStore()
    private init() {
        self.text = ""
        self.url = ""
        self.image = ""
        self.processed = false
    }

    public static func getStore() -> ShareStore {
        return store;
    }

    var text: String;
    var url: String;
    var image: String;
    var processed: Bool;
}
