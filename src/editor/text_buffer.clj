;; Local Variables:
;; eval: (put 'defn-buffer 'clojure-doc-string-elt 2)
;; End:

(ns editor.text-buffer
  (:require
    [seesaw.core :refer :all]
    [seesaw.graphics :refer :all]
    [seesaw.color :refer [color]]

    [editor.state :refer :all]
    [editor.graphics-utils :refer :all]
    [editor.utils :refer :all]
    [editor.buffer :refer :all]
    )
  (:import [java.awt Graphics FontMetrics Canvas]
           [java.awt.image BufferStrategy]))


;;https://github.com/qwerky/Towers/blob/master/src/main/java/lineup/ui/UI.java

(defn get-line-length [lines y]
  (-> lines
      (get y)
      count))

(defn move-cursor-absolute-unsafe
  "Move the cursor to `x`, `y`. Unsafe because it doesn't check the bounds.
  Only for internal usage."
  ([buffer x y] (move-cursor-absolute-unsafe buffer (->Pos x y)))
  ([buffer new-pos]
   (assoc buffer :cursor-pos new-pos)))

;; could be made a lot nicer
(defn move-cursor-relative-x
  "Move the cursor `dx` units horizontally."
  [buffer dx]
  (let [{x :x, y :y} (:cursor-pos buffer)
        
        lines (:lines buffer)
        lines-count (count lines)

        get-line-length (fn [y] (-> lines
                                   (get y)
                                   count))
        ;; when we move vertically we don't clamp the cursor
        ;; so pressing up then down leaves us in the same place (if we're not on the first line)
        x (min x (get-line-length y))
        ]
    ;; (println "MOVE" x y)
    ;; (println "MOVE" dx buffer)

    ;;TODO clamp x
    
    (move-cursor-absolute-unsafe buffer
     (loop [x (+ x dx)
            y y]
       ;; (println "rel loop" x y)
       (let [this-line-len (get-line-length y)]
         (cond
           (< x 0) (if (<= y 0)
                     (->Pos 0 0)
                     (let [newY (dec y)]
                       (recur (+ (get-line-length newY) x 1); the +1 is for a \n
                              newY)))

           (> x this-line-len) (if (>= y lines-count)
                                 (->Pos 0 lines-count)
                                 (recur (- x this-line-len 1) (inc y)))
           :else (->Pos x y)
           ))))))



(defn move-cursor-relative-y
  "Move the cursor `dy` units vertically."
  [buffer dy]
  ;; TODO
  (let [{x :x, y :y} (:cursor-pos buffer)
        lines (:lines buffer)
        new-y (clamp (+ y dy) 0 (count lines))]

    ;;We don't clamp the x
    (move-cursor-absolute-unsafe buffer x new-y)))

;; TODO TESTS

(defn-buffer move-end-of-line
  "Move the cursor to the end of current line.

  For internal usage."
  [buffer]
  (let [{x :x, y :y} (:cursor-pos buffer)
        new-x (-> buffer :lines (get y) count)]
    (move-cursor-absolute-unsafe buffer new-x y)))

(defn-buffer move-beginning-of-line
  "Move the cursor to the beginning of current line.

  For internal usage."
  [buffer]
  (let [{x :x, y :y} (:cursor-pos buffer)]
    (move-cursor-absolute-unsafe buffer 0 y)))

;; TODO somewhere else?
;; TODO macro?

;; TODO bidirectional

;; TODO interactive so we can repeat
(defn-buffer forward-char
  "Move the cursor forward.
  If `n` is specified, move `n` characters."
  ([buffer] (move-cursor-relative-x buffer 1))
  ([buffer n] (move-cursor-relative-x buffer n)))

(defn-buffer backward-char
  "Move the cursor backward.
  If `n` is specified, move `n` characters."
  ([buffer] (move-cursor-relative-x buffer -1))
  ([buffer n] (move-cursor-relative-x buffer (- n))))


(defn-buffer next-line
  "Move the cursor on the next line.
  If `n` is specified, move `n` lines."
  ([buffer] (move-cursor-relative-y buffer 1))
  ([buffer n] (move-cursor-relative-y buffer n)))

(defn-buffer previous-line
  "Move the cursor on the previous line.
  If `n` is specified, move `n` lines."
  ([buffer] (move-cursor-relative-y buffer -1))
  ([buffer n] (move-cursor-relative-y buffer (- n))))

(defn-buffer insert-newline
  "Insert a new line
   `pos` is `:cursor-pos` if unspecified."
  ([buffer]
   (insert-newline (:cursor-pos buffer)))
  ([buffer pos]
  ;;TODO
  buffer)
  )
(defn-buffer insert-char
  "Insert the character `char` at `pos`.
  `pos` is `:cursor-pos` if unspecified."
  ([buffer char]
   (insert-char char (:cursor-pos buffer)))
  ([buffer char pos]
   ;;TODO
   buffer))


;; buffer {
;;   :type :text / :drawing / ...
;;   :lines [ "bla", "bla" ]
;;   :cursorPos {:x x, :y y}
;; ...
;; }


;; text-buffer-options {
;;   :linewrap? false
;;   :linenumbers? true
;;   :cursor-type :block / :line / :half-line / ...
;;   :cursor-blink? false
;;   :font "idk"
;;  ...
;; }


(defn create-theme-from-base16
  "Convert a base16 set of colors to a theme
  see http://www.chriskempson.com/projects/base16/

  Usage:
  ```
  (create-theme-from-base16 {:base00 \"#ABCDEF\" ... :base0F \"#00ffff\"})
  ```
  "
  ;; using smth like
  ;; (mapv (fn [i] (keyword (format "base0%X" i))) (range 16))
  ;; failed, so the boring way it is:
  [color-strings]
  (let [cols (into {} (for [[k v] color-strings] [k (color v)]))
        red (:base08 cols)
        yellow (:base0A cols)
        green (:base0B cols)
        cyan (:base0C cols)
        blue (:base0D cols)

        base00 (:base00 cols) ;; Default Background
        base01 (:base01 cols) ;; Lighter Background (Used for status bars, line number and folding marks)
        base02 (:base02 cols) ;; Selection Background
        base03 (:base03 cols) ;; Comments, Invisibles, Line Highlighting
        base04 (:base04 cols) ;; Dark Foreground (Used for status bars)
        base05 (:base05 cols) ;; Default Foreground, Caret, Delimiters, Operators
        base06 (:base06 cols) ;; Light Foreground (Not often used)
        base07 (:base07 cols) ;; Light Background (Not often used)
        base08 (:base08 cols) ;; Variables, XML Tags, Markup Link Text, Markup Lists, Diff Deleted
        base09 (:base09 cols) ;; Integers, Boolean, Constants, XML Attributes, Markup Link Url
        base0A (:base0A cols) ;; Classes, Markup Bold, Search Text Background
        base0B (:base0B cols) ;; Strings, Inherited Class, Markup Code, Diff Inserted
        base0C (:base0C cols) ;; Support, Regular Expressions, Escape Characters, Markup Quotes
        base0D (:base0D cols) ;; Functions, Methods, Attribute IDs, Headings
        base0E (:base0E cols) ;; Keywords, Storage, Selector, Markup Italic, Diff Changed
        base0F (:base0F cols) ;; Deprecated, Opening/Closing Embedded Language Tags, e.g. <?php ?>
        ]
    {:background base00
     :foreground base06
     :caret yellow
     :line-numbers-background base01
     :line-numbers-foreground base03 ;;base04
     }))

;;TODO move me
;; colortest o
(def editor-options {:line-wrap? false
                     :line-numbers? true ;TODO
                     :cursor-type :block                    ;or line
                     :cursor-blink? false

                     :font (seesaw.font/font "DejaVu Sans Mono 20")
                     :theme (create-theme-from-base16
                             {:base00 "#1D1F21"
                              :base01 "#282A2E"
                              :base02 "#373B41"
                              :base03 "#7E807E"
                              :base04 "#B4B7B4"
                              :base05 "#E0E0E0"
                              :base06 "#F0F0F0"
                              :base07 "#FFFFFF"
                              :base08 "#CC342B"
                              :base09 "#F96A38"
                              :base0A "#FBA922"
                              :base0B "#198844"
                              :base0C "#12A59C" ;;"#1296A5"
                              :base0D "#3971ED"
                              :base0E "#A36AC7"
                              :base0F "#FBA922"})
                     })




(defn calculate-line-numbers-width
  [^FontMetrics font-metrics lines]

  (let [line-numbers-length (-> lines
                                count
                                number-of-digits
                                (max 3))
        left-pad 5
        right-pad 5]
    (+ (.stringWidth font-metrics
                     (apply str (repeat line-numbers-length \0)))
       left-pad
       right-pad)))


(defn get-char-at-x-y
  "Returns the caracter at position `x`, `y` in `buffer`.
  See `get-char-at` if you have a `pos`

  `buffer` is `(get-current-buffer)` by default
  Returns `nil` if outside bounds"

  ([x y] (get-char-at-x-y x y (get-current-buffer)))
  ([x y buffer]
   (-> (:lines buffer) (get y) (get x))))

(defn get-char-at
  "Returns the caracter at position `pos` in `buffer`.
  See `get-char-at-x-y` if you have a `x`, `y` pair

  `buffer` is `(get-current-buffer)` by default
  Returns `nil` if outside bounds"

  ([pos] (get-char-at pos (get-current-buffer)))
  ([pos buffer]
   (get-char-at-x-y (:x pos) (:y pos) buffer)))


(defn draw-text-buffer!
  ([canvas g buffer] (draw-text-buffer! canvas g buffer editor-options))
  ([canvas ^Graphics g buffer options]

   (println "paint!" (:cursor-pos buffer) (width canvas))
   (let [w (width canvas)
         h (height canvas)

         font (:font options)
         ^FontMetrics metrics (.getFontMetrics g font)

         char-height (.getHeight metrics)

         ;; TODO DONT USE THIS! call .stringWidth for most cases
         default-char-width (.charWidth metrics \x)

         theme (:theme options)
         lines (:lines buffer)
         line-numbers? (:line-numbers? options)

         line-numbers-width (if line-numbers?
                              (calculate-line-numbers-width metrics lines)
                              0)

         ascent (.getAscent metrics)
         ]

     (letfn [(cursor-position-to-screen
               ([pos] (cursor-position-to-screen (:x pos) (:y pos)))
               ([i j]
                (->ScreenPos
                 (+ line-numbers-width
                    (* i default-char-width))
                 (* j char-height) )))

             (clamp-x-pos [pos]
               (let [{x :x, y :y} pos]
                 (->Pos (clamp x 0 (count (get lines y))) (:y pos))
               ))

             (draw-cursor! [] ; for th e block cursor
               (let [pos (clamp-x-pos (:cursor-pos buffer))
                     
                     char (get-char-at pos buffer)

                     char-width (if char
                                  (.charWidth metrics ^char char)
                                  default-char-width)
                     w (case (:cursor-type options)
                         :block char-width
                         :line (* 0.1 char-width))
                     h char-height

                     screen-pos (cursor-position-to-screen pos)
                     ]
                 (draw-rect! g screen-pos
                             w h (:caret theme))
                 (when (= (:cursor-type options) :block)
                   (when char
                     (draw-string! g (str char) screen-pos ascent
                                   (:background theme)))
                   )
                 ;; TODO redraw the char at pos if :block cursor
                 ;; (with background)
                 ))
             ]

       (doto g
         (draw-rect! 0 0 line-numbers-width h
                     (:line-numbers-background theme))

         (draw-rect! line-numbers-width 0 (- w line-numbers-width) h
                     (:background theme))
         (.setFont font))


       (doseq [[ln value] (map-indexed vector (:lines buffer))]
         (let [y (* ln char-height)
               x line-numbers-width
               ln-str (str (inc ln))
               ln-length (count ln-str)]
           (draw-string! g value x y ascent (:foreground theme))
           (when line-numbers?
             (draw-string! g ln-str
                           (- x (* ln-length default-char-width)) y
                           ascent
                           (:line-numbers-foreground theme)))
           ))
       (draw-cursor!)
       ))))


(defn create-text-buffer
  "Create a text buffer.
   Only for internal usage."
  []
  (letfn [(on-paint [buffer]
            (let [canvas (:component buffer)
                  ^BufferStrategy bs (.getBufferStrategy canvas)
                  ^Graphics g (.getDrawGraphics bs)]
              (draw-text-buffer! canvas g buffer)
              (.show bs)))

          (on-component-added [buffer]
            (doto (:component buffer)
              (.createBufferStrategy 2);; double buffering
              ))]
    (let [buffer (create-buffer :type :text
                   :component (proxy [Canvas] []
                                (paint [g]
                                  ;;TODO do some proper drawing here
                                  ;; (proxy-super g)
                                  (println "!!og paint"))
                                  )
                   :on-paint on-paint
                   :on-component-added on-component-added
                   :lines ["abc", "Hello, world!", "█何"]
                   :cursor-pos (->Pos 1 1)
                   )
          ]
      ;; (:component buffer)

      buffer)
    ))
