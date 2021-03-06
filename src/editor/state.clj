(ns editor.state
  (:require
   [editor.buffer :as buffer]
   [seesaw.core :refer [dispose!]]

   [clojure.spec.alpha :as s]
   ;; [seesaw.color]
   )
  )

;;TODO move to buffer.clj?
(def the-state (atom nil))

(defn get-current-buffer
  "Get the currently selected buffer"
  []
  (:current-buffer @the-state))

;; ;;TODO move to editor.buffer
;; ;;TODO 
;; (defn buffer? [x]
;;   (map? x))

;; (s/fdef update-current-buffer!
;;   :args (s/cat :f (s/fspec
;;                    :args buffer?
;;                    :ret buffer?))
;;   ;; :ret buffer?
;;   )

;; TODO use the arg sent to on-key-presed?
(defn repaint-buffer!
  "Repaint the buffer.
  `buffer` is `(get-current-buffer)` by default"
  ([] (repaint-buffer! (get-current-buffer)))
  
  ([current-buffer]
   (println "repaint")
   (buffer/on-paint! current-buffer)))

(defn update-current-buffer!
  "TODO what if we have more buffers
   f : buffer, args... -> buffer"
  ([f]
   (swap! the-state #(update % :current-buffer f))
   (repaint-buffer!))

  ([f args]
   ;; (print "update! ")
   ;; (clojure.pprint/pprint @the-state)
   
   (swap! the-state #(assoc % :current-buffer
                      (apply f (get % :current-buffer) args)))
   (repaint-buffer!)
   
   ;; (print "post-update! ")
   ;; (clojure.pprint/pprint @the-state)
   )
  )


(defmacro defn-buffer
  "Use this to define interactive functions that have the first arg `buffer`.
  The function must return a buffer (ie use functions like `assoc` on the buffer)
  "
  [name & decls]
  (list* `defn (with-meta name (assoc (meta name) :buffer true)) decls))

(defmacro fn-buffer
  "Use this to define anonymus interactive functions that have the first arg `buffer`.
  The function must return a buffer (ie use functions like `assoc` on the buffer)

  See `defn-buffer`
  "
  [& decls]
  (let [res (list* `fn decls)]
    ;; `(with-meta ~res (assoc (meta ~res) :buffer true))
    `(with-meta ~res {:buffer true})
    ))


(defn call-interactively!
  "Call the function `f` with the args, providing the current buffer if needed.

  If the function was defined with `defn-buffer` the current buffer is passed as a param.

  Usage:
  ```
  (call-interactively #'forward-char 42)
  (call-interactively #'forward-char)
  ```
  "
  ([f]
   (if (:buffer (meta f))
     (update-current-buffer! f)
     (f)))
  ([f & args]
   (if (:buffer (meta f))
     (update-current-buffer! f args)
     (apply f args)))
  )

(def the-gui (atom nil))

(defn get-gui
  "Get the current gui (JFrame)"
  []
  @the-gui)

(defn quit-gui! []
  (dispose! (get-gui)))
