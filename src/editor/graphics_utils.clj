(ns editor.graphics-utils
  "Graphics utilities and helper functions"
  ;; (:require
    ;; [seesaw.core :refer :all]
    ;; [seesaw.graphics :refer :all]
    ;; [seesaw.color :refer [color]]
    ;; [editor.state :refer :all]
    ;; )
  (:import (java.awt Graphics)))

;; make pos a record?

(defn ->Pos
  "A position in text space (line `y`, column `x`)"
  [x y]
  {:x x :y y})

;; (defn Pos+
;;   "Add 2 `Pos` objects"
;;   [a b]
;;   (let [{ax :x ay :y} a
;;         {bx :x by :y} b]
;;     (->Pos (+ ax bx) (+ ay by))))

(defn ->ScreenPos
  "A screen position.
  The same as `->Pos`, but different names for
  different things"
  [x y]
  {:x x :y y})


(defn draw-rect!
  "Draw a rectangle with `color`.
  clobbers the color"
  ([^Graphics g x y w h color]
   (.setColor g color)
   (.fillRect g x y w h))
  
  ([^Graphics g pos w h color]
   (draw-rect! g (:x pos) (:y pos) w h color)))

(defn draw-string!
  "Draws `string` with `color`.
  clobbers the color
  
  Normally `x`,`y` are the coords of the baseline,
  but we add `ascent` to `y` so `x`,`y` is the top left of the text.
  "
  ([^Graphics g string x y ascent color]
   ;; (println "DRAW Štring" string (type string))
   (.setColor g color)
   (.drawString g ^String string ^int x ^int (+ y ascent)))

  ([g string pos ascent color]
   (draw-string! g string (:x pos) (:y pos) ascent color)))
