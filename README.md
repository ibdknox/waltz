# waltz

Waltz is a ClojureScript library that helps manage state in client-side applications using
non-deterministic finite state machines.

## Usage

Here's an example using waltz, crate, jayq, and fetch:

```clojure
(ns metrics.client.main
  (:require [waltz.state :as state]
            [crate.core :as crate]
            [fetch.lazy-store :as store])
  (:use [jayq.core :only [append $ find show hide inner add-class remove-class]]
        [waltz.state :only [trigger]])
  (:use-macros [waltz.macros :only [in out defstate deftrans]]
               [crate.macros :only [defpartial]]))

(defn wait [ms func]
  (js* "setTimeout(~{func}, ~{ms})"))

(def $container ($ :#metricsContainer))

(defpartial metric [{:keys [klass label]}]
  [:div {:class (str "metric " klass)}
   [:p.loading "Loading..."]
   [:h1.value "..."]
   [:h2 label]])

(defn add-metric [params]
  (let [$elem ($ (metric params))
        $loading (find $elem :.loading)
        $value (find $elem :.value)
        delay (or (:delay params) 10000)
        me (state/machine (:label params))]

    (defstate me :loading
      (in [] (show $loading))
      (out [] (hide $loading)))

    (defstate me :normal
      (in [v]
        (inner $value v)
        (wait delay #(trigger me :update))))

    (defevent me :update []
      (state/set me :loading)
      (store/latest [:metrics (:metric params)]
                    #(trigger me :set %)))

    (defevent me :set [v]
      (state/unset me :loading)
      (state/set me :normal v))

    (trigger me :update)

    (append $container $elem)
    me))

(add-metric {:label "Views today" :klass "gray"})
(add-metric {:label "Signups today" :delay 30000 :klass "gray"})
(add-metric {:label "Signups today" :delay 30000 :klass "gray"})
(add-metric {:label "Signups today" :delay 30000 :klass "gray"})
(add-metric {:label "Signups today" :delay 30000 :klass "gray big"})
(add-metric {:label "Signups today" :delay 30000 :klass "gray big"})
```

## License

Copyright (C) 2011 Chris Granger

Distributed under the Eclipse Public License, the same as Clojure.
