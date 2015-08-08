(ns ^:figwheel-always cljs-games.core
    (:require [cljs-games.pexeso :as pexeso]
              [reagent.core :as reagent :refer [atom]]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; init

(defn init []
  (pexeso/mount-root))

(defn on-js-reload []
  (init))

(init)
