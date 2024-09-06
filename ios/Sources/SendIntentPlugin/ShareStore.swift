import Foundation
import Capacitor

public final class ShareStore {

    public static let store = ShareStore()

    private init() {
        self.shareItems = []
        self.processed = false
    }

    public var shareItems: [JSObject]

    public var processed: Bool
}
