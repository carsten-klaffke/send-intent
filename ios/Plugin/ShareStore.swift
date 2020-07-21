import Foundation

public final class ShareStore {

    public static let store = ShareStore()
    private init() {
        self.text = ""
        self.url = ""
        self.image = ""
        self.processed = false
    }

    public var text: String;
    public var url: String;
    public var image: String;
    public var processed: Bool;
}
