import component.fcContainerLesson
import component.fcContainerStudent
import component.fcContainerStudentList
import component.fcContainerLessonList
import kotlinx.browser.document
import kotlinx.css.WhiteSpace
import kotlinx.css.margin
import kotlinx.css.px
import kotlinx.css.whiteSpace
import react.createElement
import react.dom.render
import react.query.QueryClient
import react.query.QueryClientProvider
import react.router.Route
import react.router.Routes
import react.router.dom.HashRouter
import react.router.dom.Link
import styled.css
import styled.styledSpan
import wrappers.cReactQueryDevtools

val queryClient = QueryClient()

fun main() {
    render(document.getElementById("root")!!) {
        HashRouter {
            QueryClientProvider {
                attrs.client = queryClient
                styledSpan {
                    css {
                        whiteSpace = WhiteSpace.preWrap
                        margin(top = 10.px, bottom = 10.px)
                    }
                    Link {
                        attrs.to = "/students"
                        +"Students"
                    }
                    +"   "
                    Link {
                        attrs.to = "/lessons"
                        +"Lessons"
                    }
                }
                Routes {
                    Route {
                        attrs.path = "/students"
                        attrs.element =
                            createElement(fcContainerStudentList())
                    }
                    Route {
                        attrs.path = "/students/:id"
                        attrs.element =
                            createElement(fcContainerStudent())
                    }
                    Route {
                        attrs.path = "/lessons"
                        attrs.element =
                            createElement(fcContainerLessonList())
                    }
                    Route {
                        attrs.path = "/lessons/:id"
                        attrs.element =
                            createElement(fcContainerLesson())
                    }
                }
                child(cReactQueryDevtools()) {}
            }
        }
    }
}

