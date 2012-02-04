(ns waltz.history
  (:refer-clojure :exclude [set get])
  (:require [clojure.browser.event :as event]
            [goog.History :as history]
            [goog.history.Html5History :as history5]))

(defn create-history []
  (let [h (if (history5/isSupported)
            (goog.history.Html5History.)
            (goog.History.))]
    (.setEnabled h true)
    h))

(def history (create-history))

(defn set [token]
  (.setToken history (name token)))

(defn get []
  (let [t (.getToken history)]
    (if (= "" t)
      nil
      (keyword t))))

(defn listen [callback]
  (event/listen history "navigate" callback))
