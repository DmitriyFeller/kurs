import component.*
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
                        attrs.to = "/groups"
                        +"Groups"
                    }
                    +"   "
                    Link {
                        attrs.to = "/cabinets"
                        +"Cabinets"
                    }
                    +"   "
                    Link {
                        attrs.to = "/lessons"
                        +"Lessons"
                    }
                    +"   "
                    Link {
                        attrs.to = "/schedule"
                        +"Schedule"
                    }
                }
                Routes {
                    //groups: view list, add in list
                    Route {
                        attrs.path = "/groups"
                        attrs.element = createElement(fcContainerGroupList())
                    }
                    //cabinets: view list, add in list, remove from list
                    Route {
                        attrs.path = "/cabinets"
                        attrs.element = createElement(fcContainerCabinetList())
                    }
                    //lessons: view list, open element
                    Route {
                        attrs.path = "/lessons"
                        attrs.element = createElement(fcContainerLessonList())
                    }
                    //lesson: view element
                    Route {
                        attrs.path = "/lessons/:id"
                        attrs.element = createElement(fcContainerLesson())
                    }
                    //schedule: view list, open element
                    Route {
                        attrs.path = "/schedule"
                        attrs.element = createElement(fcContainerScheduleList())
                    }
                    //schedule element: view, modify sub-elements
                    Route {
                        attrs.path = "/schedule/:id"
                        attrs.element = createElement(fcContainerSchedule())
                    }
                }
                child(cReactQueryDevtools()) {}
            }
        }
    }
}

