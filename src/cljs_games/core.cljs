(ns ^:figwheel-always cljs-games.core
    (:require [cljs-games.pexeso :as pexeso]
              [cljs-games.x-and-zero :as x-and-zero]
              [cljs-games.game-of-life :as gol]

              [reagent.core :as reagent :refer [atom]]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; init

(defn init []
  #_(pexeso/mount-root)
  #_(gol/draw-board(gol/init-random-board 10))
  #_(x-and-zero/mount-root))

(defn on-js-reload []
  (init))

(init)
