(ns waltz.transition
  (:require [waltz.history :as history]
            [waltz.state :as state])
  (:use [waltz.state :only [trigger debug-log]]))

(defn exclude [sm name to-set to-unset]
  (state/add-event sm name (fn [& args]
                             (apply state/unset sm to-unset args)
                             (apply state/set sm to-set args))))

(defn by-url [sm]
  (let [url (.-location.pathname js/window)]
    (trigger sm [:url url])))

(defn by-hash [sm]
  (history/listen (fn [e]
                    (let [token (.-token e)
                          token (if (= "" token)
                                  "index"
                                  token)
                          type (.-type e)
                          navigation? (.-isNavigation e)
                          kw (keyword (str "hash:" token))]
                      (debug-log sm "hash keyword: " kw)
                      (debug-log sm "hash changed: " token " :: navigation? " navigation? " :: type " type)
                      (when navigation?
                        (trigger sm kw))))))


