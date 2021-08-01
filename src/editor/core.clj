(ns editor.core
  (:gen-class)

  (:import
   [javax.swing SwingUtilities JFrame JLabel JPanel]
   [java.awt GraphicsEnvironment GraphicsDevice Color Font Canvas]
   [java.awt.event
    KeyEvent KeyListener
    ComponentListener ComponentEvent]
   ;; [javla LineNumberingTextArea]
   )
  (:require
   [editor.utils :refer [case-with-eval case-enum]]
   [editor.text-buffer :refer :all]
   [editor.state :refer :all]

   [editor.buffer :refer :all]


   ;;for ease of replness
   ;; [editor.graphics :refer :all]
   ;; [seesaw.color]
   )

  ;; (:import
   ;; [java.awt Color])
  (:use clojure.pprint)
  (:use seesaw.color)
  (:use seesaw.dev)
  (:use seesaw.font)
  (:use seesaw.core)
  )


;; https://cemerick.com/blog/2011/07/05/flowchart-for-choosing-the-right-clojure-type-definition-form.html


;; (defn the-gui-get-current-canvas []
;;   (:current-canvas @the-state))


(defn init-state! []
  ;; (let [buffer (cre)]
  (reset! the-state {:current-buffer (create-text-buffer)
                     ;; TODO multiple buffers
                     ;; :buffers []

                     ;; TODO multiple canvases
                     ;; might not be needed
                     ;; :current-canvas nil
                     }))

;; https://github.com/clj-commons/seesaw/tree/develop/test/seesaw/test/examples
;; https://docs.oracle.com/javase/tutorial/uiswing/misc/trans_shaped_windows.html

(defn make-transparent!
  [root]

  (doto root
      (.setUndecorated true)
      (.setBackground (color 0 0xff 0 50)))
  root)


;;TODO extended modifier or whatever
(def mod-ctrl 2)
(def mod-shift 1)
(def no-mod 0)

(defn solve-key
  "Returns the function that should be called for the keypress.
  Returns `nil` if no keybinding is assigned."
  [modifiers code char]
  (case-with-eval
   modifiers
   mod-ctrl (case-with-eval code
                            KeyEvent/VK_Q #'quit-gui!
                            KeyEvent/VK_F #'forward-char
                            KeyEvent/VK_A #'backward-char
                            nil)
   no-mod (case-with-eval code
                          KeyEvent/VK_RIGHT #'forward-char
                          KeyEvent/VK_LEFT #'backward-char

                          KeyEvent/VK_UP #'previous-line
                          KeyEvent/VK_DOWN #'next-line

                          KeyEvent/VK_END #'move-end-of-line
                          KeyEvent/VK_HOME #'move-beginning-of-line
                          ;; KeyEvent/VK_ENTER 
                          ;; (if (Character/isIdentifierIgnorable char)
                          ;;   ;; (not (Character/isValidCodePoint char)))
                          ;;   nil
                          ;;   (fn-buffer [buffer]
                          ;;              (println "chr" char)
                          ;;              (insert-char buffer char)))
                          nil
                          )
   nil))

(defn on-key-pressed! [e]
  (let [ ;;key (.getKeyChar e)
        extended-code (.getExtendedKeyCode e)
        modifiers (.getModifiers e)
        char (.getKeyChar e)]
    ;; (println e)
    (println "key-down:" char "-" extended-code ";" modifiers)
    (if-let [fun (solve-key modifiers extended-code char)]
      (do
        (call-interactively! fun)
        true)
      false
    )
    ;; true
    ))

(defn on-key-typed! [e]
  (println "okt")
  (let [chr (.getKeyChar e)]
    (println "typed" chr)
    (case chr

      \u007f (call-interactively! #'delete-forward)
      \backspace (call-interactively! #'delete-backward)
      \newline (call-interactively! #'insert-newline)
      (call-interactively! #'insert-char chr))
    )
  true)


(defn create-frame-contents [current-buffer]
  (let [value (border-panel
               :center (-> current-buffer :component)
               :background "#ff0000"
               :border 5
               )]
    ;; (swap! the-state assoc :current-pane value)
    value))

;;TODO call post- show for new stuff
(defn post-show!
  [gui state]

  (let [current-buffer (get-current-buffer)]
    (doto (:component current-buffer)
      (.addKeyListener (proxy [KeyListener] []
                         (keyPressed [e] (on-key-pressed! e))
                         (keyReleased [e])
                         (keyTyped [e] (on-key-typed! e))))
      (.addComponentListener (proxy [ComponentListener] []
                               (componentShown [e]
                                 (repaint-buffer! current-buffer)
                                 (println "comp shown")
                                 )
                               (componentResized [e]
                                 (repaint-buffer! current-buffer)
                                 (println "comp resized")
                                 )
                               (componentMoved [e]
                                 (repaint-buffer! current-buffer)
                                 (println "comp moved")
                                 )
                               )))

    (on-component-added! current-buffer)

    (repaint-buffer! current-buffer)
    (println "-added component")
    )

    gui)

;; (swap! uses a function)
(defn init-gui! [state]
  (let [content (create-frame-contents (:current-buffer state))]
    (reset! the-gui
            (frame :title "Editor"
                   ;; :class
                   :content content
                   :listen [:key-pressed #'on-key-pressed!
                            ;; TODO focus the window so we don't do this
                            :key-typed #'on-key-typed!
                            ;; only resized and shown?
                            :component-resized (fn [e]
                                                 (println "cs!")
                                                 (repaint-buffer!))
                            ]
                   ;; :background :blue
                   :on-close :dispose ;;:exit
                   :width 800
                   :height 600
                   ))
    (-> (get-gui)
        make-transparent!

        ;; add-keys!

        pack!
        show!
        (post-show! state))))




(defn -main [& args]
  (init-state!)
  ;;(load-file "src/editor/test.clj")

  ;; (let [thing (new editor.core.PicturePanel)])

  (invoke-later
   (init-gui! @the-state)
   ;; (repaint-buffer!)
   )
  )
