(ns ^:figwheel-always cljs-games.core
    (:require [cljs-games.pexeso :as pexeso]
              [cljs-games.game-of-life :as gol]

              [reagent.core :as reagent :refer [atom]]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; init

(defn init []
  (pexeso/mount-root)
  (gol/draw-board(gol/init-random-board 10)))

(defn on-js-reload []
  (init))

(init)
